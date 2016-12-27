
package Threads;

import Constants.ClientServerRequests;
import Constants.Constants;
import DataMessaging.ClientServerMessage;
import DataMessaging.DataAddress;
import DataMessaging.ServerMessage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import DataMessaging.Login;
import Exceptions.WriteOnFileException;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class AttendTCPClientsThread extends Thread implements Constants, ClientServerRequests{

    private Socket toClientSocket = null;
    private DataAddress myAddress = null;
    private InetAddress directoryServiceIP = null;
    private int directoryServicePort = -1;
    private List<DataAddress> usersLoggedIn = null;
    private List<Login> loginsList = null;
    private File rootDirectory = null;
    private String loginFile = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private ClientServerMessage requestMessage = null;
    
    public AttendTCPClientsThread(Socket socket, DataAddress myAddress, InetAddress dsIP, int dsPort,
            List<DataAddress> users, File rootDirectory, String loginFile){
        this.toClientSocket = socket;
        this.myAddress = myAddress;
        this.directoryServiceIP = dsIP;
        this.directoryServicePort = dsPort;
        this.usersLoggedIn = users;
        this.loginsList = new ArrayList<>();
        this.rootDirectory = rootDirectory;
        this.loginFile = loginFile;
        this.updateLoginsList();
    }
    
    @Override
    public void run(){
        try {
            boolean success = false;
            while(true){
                success = false;
                System.out.println("estou a atender um cliente!");
                // <editor-fold defaultstate="collapsed" desc=" Prepare ObjectOutput and ObjectInput stream ">
                out = new ObjectOutputStream(toClientSocket.getOutputStream());
                in = new ObjectInputStream(toClientSocket.getInputStream());
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Receive requestMessage ">
                requestMessage = (ClientServerMessage)in.readObject();
                System.out.println("Request - " + requestMessage.getRequest());
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Attend request ">
                switch(requestMessage.getRequest()){
                    // <editor-fold defaultstate="collapsed" desc=" LOGIN">
                    case LOGIN:
                        //1º Verificar se username e password estão correctos
                        if(this.isUsernameAndPasswordCorrect(requestMessage.getLogin())){
                        //2º Só adiciona se não existir
                            if(!this.usernameAlreadyExists(requestMessage.getLogin()))
                                this.addUserToList(requestMessage.getClientAddress());
                            success = true;
                        }else{
                            success = false;
                        }
                        requestMessage.setSuccess(success);
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" LOGOUT ">
                    case LOGOUT:
                        if(this.isUserLoggedIn(requestMessage.getLogin())){
                            this.removeUserFromList(requestMessage.getClientAddress());
                            success = true;
                        }else{
                            success = false;
                        }
                        requestMessage.setSuccess(success);
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" CREATE ACCOUNT ">
                    case CREATE_ACCOUNT:
                        if(this.usernameAlreadyExists(requestMessage.getLogin())){
                            success = false;
                        }else{
                            try {
                                success = this.CreateAccount(requestMessage.getLogin(), requestMessage.getClientAddress());
                            } catch (WriteOnFileException ex) {
                                System.err.println(ex);
                                success = false;
                            }
                        }
                        System.out.println("Success = " + success);
                        requestMessage.setSuccess(success);
                        break;
                    // </editor-fold>
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Write response ">
                out.writeUnshared(requestMessage);
                out.flush();
                // </editor-fold>                
            }    
        } catch (ClassNotFoundException | IOException ex) {
                System.out.println("Client " + 
                        toClientSocket.getInetAddress().getHostAddress() + ":" +
                        toClientSocket.getPort() + " communication error\n\t" + ex);
        } finally{
            if(this.toClientSocket != null){
                try {
                    this.toClientSocket.close();
                } catch (IOException ex1) {
                    System.err.println(ex1);
                }
            } 
            this.removeUserFromList(requestMessage.getClientAddress());
        }
        
    }
    
    /**
     * Este método é chamado pelo construtor para ir buscar a lista de Clientes(Login) registados no ficheiro txt
     */
    private void updateLoginsList() {
        String linha = null;
        String username = null;
        String password = null;
        BufferedReader inFile = null;
        
        this.loginsList.clear();
        
        try {
            
            inFile = new BufferedReader(new FileReader(this.loginFile));
            
            synchronized(inFile){
                while((linha = inFile.readLine()) != null){

                    linha = linha.trim();
                    if(linha.length() == 0){
                        continue;
                    }

                    Scanner scan = new Scanner(linha);

                    try{                    
                        username = scan.next();
                        password = scan.next();

                        this.loginsList.add(new Login(username, password));

                    }catch(Exception e){
                        System.err.print("> Incorrect entry in file ");
                        System.err.println(this.loginFile + ": \"" + linha + "\"");
                        continue;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println();
            System.err.println("File impossible to open: " + this.loginFile + "\n\t" + ex);
        } catch (IOException ex) {
            System.err.println(); 
            System.err.println(ex);
        } finally{
            try {
                if(inFile != null){
                    inFile.close();
                }
            } catch (IOException ex) { }
        }
    }
   
    /**
     * Este método vai ser chamado:
     *      -   createAccount
     */
    private boolean usernameAlreadyExists(Login login){
        for(Login l : this.loginsList){
            if(l.equals(login.getUsername()))
                return true;
        }
        
        return false;
    }
    
    /**
     * Este método vai ser chamado:
     *      -   login
     */
    private boolean isUsernameAndPasswordCorrect(Login login){
        for(Login l : this.loginsList){
            if(l.equals(login))
                return true;
        }
        
        return false;
    }
    
    /**
     * Este método vai ser chamado:
     *      -   logout
     */
    private boolean isUserLoggedIn(Login login){
        for(Login l : this.loginsList){
            if(login.equals(l))
                return true;
        }
        return false;
    }
    
    /**
     * Este método vai ser chamado:
     *      -   login
     *      -   createAccount
     */
    private void addUserToList(DataAddress clientAddress){
        synchronized(this.usersLoggedIn){
            this.usersLoggedIn.add(clientAddress);
        }
        this.notifyDirectoryServiceAboutUsersList();
    }
    
    /**
     * Este método vai ser chamado:
     *      -   logout
     *      -   caso haja algum erro de comunicação com o cliente
     */
    private void removeUserFromList(DataAddress clientAddress){
        boolean isToNofify = false;
        
        synchronized(this.usersLoggedIn){
            for(DataAddress dataAddress : this.usersLoggedIn){
                if(dataAddress.equals(clientAddress)){
                    this.usersLoggedIn.remove(clientAddress);
                    isToNofify = true;
                    break;
                }
            }
        }
        
        if(isToNofify){
            this.notifyDirectoryServiceAboutUsersList();
        }
    }
    
    /**
     * Este método vai ser chamado:
     *      -   createAccount
     */
    private void addLoginToFile(Login login) throws WriteOnFileException{
        BufferedWriter bWriter  = null;
        try {
            
            bWriter = new BufferedWriter(new FileWriter(this.loginFile, true));
            synchronized(bWriter){
                bWriter.write(login.toStringLoginFormat());
            }
            
            this.updateLoginsList();
        } catch (IOException ex) {
            throw new WriteOnFileException(this.loginFile, ex.toString());
        } finally {
            try {
                if(bWriter != null)
                    bWriter.close();
            } catch (IOException ex) {
                throw new WriteOnFileException(this.loginFile, ex.toString());
            }
        }
    }
    
    /**
     * Este método vai ser chamado em 2 situações:
     *      -   adduserToList
     *      -   removeUserFromList
     */
    private void notifyDirectoryServiceAboutUsersList(){
        
        try {
            
            DatagramSocket socketUDP = new DatagramSocket();
            
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();            
            ObjectOutputStream out = new ObjectOutputStream(bOut);

            ServerMessage serverMessage = new ServerMessage(this.myAddress, this.usersLoggedIn, SERVER_MSG_UPDATE_LIST, false);
            
            out.writeObject(serverMessage);
            out.flush();
            
            DatagramPacket packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), this.directoryServiceIP, this.directoryServicePort);
            socketUDP.send(packet);
            
        } catch (SocketException ex) {
            System.out.println("An error occurred with the UDP socket level:\n\t" + ex);
        } catch (IOException ex) {
            System.out.println("An error occurred in accessing the socket:\n\t" + ex);
        }
    }

    /**
     * Este método vai ser chamado:
     *      -   create account
     */
    private boolean CreateAccount(Login login, DataAddress clientAddress) throws WriteOnFileException {
        //1º verificar se já existe
        if(this.usernameAlreadyExists(login))
            return false;
        
        //2º adicionar ao ficheiro de texto
        try {
            this.addLoginToFile(login);
        } catch (WriteOnFileException ex) {
            throw new WriteOnFileException(this.loginFile, ex.toString());
        }
        
        //3º adicionar utilizador à lista
        this.addUserToList(clientAddress);
        
        //4º criar pasta raiz para esse username
        File file = new File(this.rootDirectory + File.separator + login.getUsername());
        if(!file.mkdir())
            return false;
        
        return true;
    }

    private boolean GetFilesInDirectory(File directory) {
        File[] files = new File(this.rootDirectory + File.separator + directory).listFiles();
            
        for(int i = 0; i < files.length; i++){
            if(files[i].isDirectory())
                System.out.println("Pumba! sou uma pasta " + files[i]);
            else
                System.out.println("ohhh :( sou um ficheiro " + files[i]);
        }
            
        return true;    
    }
}
