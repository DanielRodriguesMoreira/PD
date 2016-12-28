
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
import java.util.Arrays;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class AttendTCPClientsThread extends Thread implements Constants, ClientServerRequests{
    private static final String REMOTE_NAME = "remote";
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
    
    private String clientWorkingDir = null;
    private String clientParentWorkingDir = null;
    
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
                            if(!this.isUserLoggedIn(requestMessage.getClientAddress())){
                                this.addUserToList(requestMessage.getClientAddress());
                            }
                            success = true;
                            this.setClientWorkingDir(requestMessage.getLogin().getUsername());
                        }else{
                            success = false;
                        }
                        requestMessage.setSuccess(success);
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" LOGOUT ">
                    case LOGOUT:
                        if(this.isUserLoggedIn(requestMessage.getClientAddress())){
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
                        try {
                            success = this.CreateAccount(requestMessage.getLogin(), requestMessage.getClientAddress());
                        } catch (WriteOnFileException ex) {
                            System.err.println(ex);
                            success = false;
                        }
                        System.out.println("Success = " + success);
                        requestMessage.setSuccess(success);
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" GET WORKING DIR PATH ">
                    case GET_WORKING_DIR_PATH:
                        requestMessage.setWorkingDirectoryPath(this.getClientWorkingDir());
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" GET WORKING DIR CONTENT">
                    case GET_WORKING_DIR_CONTENT:
                        requestMessage.setWorkingDirectoryContent(this.getWorkingDirContent());
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" CHANGE DIRECTORY ">
                    case CHANGE_DIRECTORY:                                                                                          //FALTA VERIFICAR ERROS path pode vir a null ou ser uma path que não exista
                        this.setClientWorkingDir(requestMessage.getPathToChange());
                        requestMessage.setWorkingDirectoryContent(this.getWorkingDirContent());
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
    private boolean usernameAlreadyExistsInFile(Login login){
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
    private boolean isUserLoggedIn(DataAddress clientAddress){
        for(DataAddress da : this.usersLoggedIn){
            if(da.equals(clientAddress))
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
            
            System.out.println("Mandei para o directory service:");
            for(DataAddress da : this.usersLoggedIn){
                System.out.println(da.getName());
            }
            
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
        if(this.usernameAlreadyExistsInFile(login))
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
    
    private ArrayList<File> getWorkingDirContent(){       
        File[] file = new File(this.rootDirectory + File.separator + this.getClientWorkingDir()).listFiles();
        
        ArrayList<File> filesToSend = new ArrayList<>(Arrays.asList(file));
        filesToSend.add(new File("(Ola Tiago)"));
        
        return filesToSend;
    }
    
    private String getClientWorkingDir(){
        return this.clientWorkingDir;
    }
    
    private void setClientWorkingDir(String newWorkingDir){
        //this.clientParentWorkingDir = this.clientWorkingDir;
        this.clientWorkingDir = newWorkingDir + File.separator;
    }
    
    private String getClientParentWorkingDir(){
        return this.clientParentWorkingDir;
    }
    
    private void setClientParentWorkingDir(){
        
    }
}
