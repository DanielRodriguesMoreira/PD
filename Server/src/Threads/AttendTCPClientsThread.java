
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
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

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
    private List<DataAddress> usersLoggedIn = null; //Lista de clientes logados
    //private List<String> usersNamesLoggedIn = null;
    private List<Login> loginsList = null;  //Lista de username/password que lê do ficheiro
    private File rootDirectory = null;
    private String loginFile = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private ClientServerMessage requestMessage = null;
    
    private String clientActualDir = "";
    private String clientWorkingDir = "";
    private String clientRootDir = "";
    private String clientWorkingDirPathToShow = "";
    
    public AttendTCPClientsThread(Socket socket, DataAddress myAddress, InetAddress dsIP, int dsPort,
            List<DataAddress> users, File rootDirectory, String loginFile, List<String> usernamesLoggedIn){
        this.toClientSocket = socket;
        this.myAddress = myAddress;
        this.directoryServiceIP = dsIP;
        this.directoryServicePort = dsPort;
        this.usersLoggedIn = users;
        this.loginsList = new ArrayList<>();
        this.rootDirectory = rootDirectory;
        this.loginFile = loginFile;
        this.updateLoginsList();
        //this.usersNamesLoggedIn = usernamesLoggedIn;
    }
    
    @Override
    public void run(){
        try {
            boolean success = false;
            while(true){
                success = false;
                // <editor-fold defaultstate="collapsed" desc=" Prepare ObjectOutput and ObjectInput stream ">
                out = new ObjectOutputStream(toClientSocket.getOutputStream());
                in = new ObjectInputStream(toClientSocket.getInputStream());
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Receive requestMessage ">
                requestMessage = (ClientServerMessage)in.readObject();
                System.out.println("[AttendTCPClientsThread]Request - " + requestMessage.getRequest());
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Attend request ">
                switch(requestMessage.getRequest()){
                    // <editor-fold defaultstate="collapsed" desc=" LOGIN">
                    case LOGIN:
                        //1º Verificar se username e password estão correctos
                        //2º Só adiciona se não existir
                        if(this.isUsernameAndPasswordCorrect(requestMessage.getLogin()) 
                                && !this.isUserLoggedIn(requestMessage.getClientAddress()) ) {
                            this.addUserToList(requestMessage.getClientAddress());
                            success = true;
                            // <editor-fold defaultstate="collapsed" desc=" Configure directories ">
                            this.setClientWorkingDir(requestMessage.getLogin().getUsername());
                            this.clientRootDir = this.getClientWorkingDir();
                            // </editor-fold>
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
                            resetWorkingDir();
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
                            System.err.println("[AttendTCPClientsThread]" + ex);
                            success = false;
                        }
                        requestMessage.setSuccess(success);
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" GET WORKING DIR PATH ">
                    case GET_WORKING_DIR_PATH:
                        requestMessage.setWorkingDirectoryPath(this.convertWorkingDirToShow());
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" GET WORKING DIR CONTENT">
                    case GET_WORKING_DIR_CONTENT:
                        requestMessage.setWorkingDirectoryContent(this.getWorkingDirContent());
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" CHANGE DIRECTORY ">
                    case CHANGE_DIRECTORY:
                        this.setClientWorkingDir(requestMessage.getPathToChange());
                        requestMessage.setWorkingDirectoryContent(this.getWorkingDirContent());
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" MAKE DIR ">
                    case MAKE_NEW_DIR:
                        success = this.makeDir(requestMessage.getNewDirName());
                        requestMessage.setWorkingDirectoryContent(this.getWorkingDirContent());
                        requestMessage.setSuccess(success);
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" REMOVE ">
                    case REMOVE:
                        success = this.removeEmptyDirOrFile(requestMessage.getNewDirName());
                        requestMessage.setWorkingDirectoryContent(this.getWorkingDirContent());
                        requestMessage.setSuccess(success);
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" COPY&PASTE ">
                    case COPY_AND_PASTE:
                        success = this.copyAndPaste(requestMessage.getOriginalFilePath());
                        requestMessage.setWorkingDirectoryContent(this.getWorkingDirContent());
                        requestMessage.setSuccess(success);
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" DOWNLOAD ">
                    case DOWNLOAD:
                        try{
                            requestMessage.setFileContent(this.downloadFile(requestMessage.getOriginalFilePath()));
                            success = true;
                        }catch(IOException ex){
                            success = false;
                        }
                        requestMessage.setSuccess(success);
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" UPLOAD ">
                    case UPLOAD:
                        success = this.uploadFile(requestMessage.getFileContent(), requestMessage.getNewDirName());
                        requestMessage.setWorkingDirectoryContent(this.getWorkingDirContent());
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
                System.out.println("[AttendTCPClientsThread]Client " + 
                        toClientSocket.getInetAddress().getHostAddress() + ":" +
                        toClientSocket.getPort() + " communication error\n\t" + ex);
        } finally{
            if(this.toClientSocket != null){
                try {
                    this.toClientSocket.close();
                } catch (IOException ex1) {
                    System.err.println("[AttendTCPClientsThread]" + ex1);
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
                        System.err.print("[AttendTCPClientsThread]> Incorrect entry in file ");
                        System.err.println("[AttendTCPClientsThread]" + this.loginFile + ": \"" + linha + "\"");
                        continue;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println();
            System.err.println("[AttendTCPClientsThread]File impossible to open: " + this.loginFile + "\n\t" + ex);
        } catch (IOException ex) {
            System.err.println(); 
            System.err.println("[AttendTCPClientsThread]" + ex);
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
            if(da.equals(clientAddress)) {
                return true;
            }
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
            System.out.println("[AttendTCPClientsThread]An error occurred with the UDP socket level:\n\t" + ex);
        } catch (IOException ex) {
            System.out.println("[AttendTCPClientsThread]An error occurred in accessing the socket:\n\t" + ex);
        }
    }

    /**
     * Este método vai ser chamado:
     *      -   create account
     */
    private boolean CreateAccount(Login login, DataAddress clientAddress) throws WriteOnFileException {
        //1º verificar se já existe no ficheiro
        if(this.usernameAlreadyExistsInFile(login))
            return false;
        
        //2º adicionar ao ficheiro de texto
        try {
            this.addLoginToFile(login);
        } catch (WriteOnFileException ex) {
            throw new WriteOnFileException(this.loginFile, ex.toString());
        }
        
        //3º adicionar utilizador à lista dos clientes logados
        this.addUserToList(clientAddress);
        
        //5º adicionar a lista de logins
        this.loginsList.add(login);
        
        //6º criar pasta raiz para esse username
        File file = new File(this.rootDirectory + File.separator + login.getUsername());
        if(!file.mkdir())
            return false;
        // <editor-fold defaultstate="collapsed" desc=" Configure directories ">
        this.setClientWorkingDir(requestMessage.getLogin().getUsername());
        this.clientRootDir = this.getClientWorkingDir();
        // </editor-fold>
        
        return true;
    }
    
    private ArrayList<File> getWorkingDirContent(){  
        System.out.println("[AttendTCPClientsThread]I'll search the folder: " + this.rootDirectory + File.separator + this.getClientWorkingDir());
        File[] file = new File(this.rootDirectory + File.separator + this.getClientWorkingDir()).listFiles();
        ArrayList<File> filesToSend = new ArrayList<>(Arrays.asList(file));

        return filesToSend;
    }
    
    private String getClientWorkingDir(){
        return this.clientWorkingDir;
    }
    
    private void setClientWorkingDir(String newWorkingDir){
        if(newWorkingDir.equals(new String("[ " + this.clientWorkingDirPathToShow + " ]"))){
            newWorkingDir = newWorkingDir.replace("[ ", "");
            newWorkingDir = newWorkingDir.replace(" ]", "");
            newWorkingDir = newWorkingDir.replace(("remote" + this.myAddress.getName() + File.separator), this.rootDirectory + File.separator + this.clientRootDir);
            newWorkingDir = newWorkingDir.substring(0, newWorkingDir.lastIndexOf(this.clientActualDir));
            newWorkingDir = newWorkingDir.replace(this.rootDirectory + File.separator, "");

            this.clientWorkingDir = newWorkingDir;
            this.clientActualDir = newWorkingDir.substring(0, newWorkingDir.lastIndexOf(this.clientRootDir));
        }else{
            this.clientActualDir = newWorkingDir + File.separator;
            this.clientWorkingDir += newWorkingDir + File.separator;
        }
    }
    
    private void resetWorkingDir(){
        this.clientWorkingDir = "";
        this.clientActualDir = "";
    }
    
    private String convertWorkingDirToShow(){
        String aux = this.rootDirectory + File.separator + this.getClientWorkingDir();
        clientWorkingDirPathToShow = aux.replace(this.rootDirectory + File.separator + this.clientRootDir, ("remote" + this.myAddress.getName() + File.separator));
        return clientWorkingDirPathToShow;
    }

    private boolean makeDir(String newDirName) {
        File file = new File(this.rootDirectory + File.separator + this.getClientWorkingDir() + newDirName);
        return file.mkdir();
    }

    private boolean removeEmptyDirOrFile(String newDirName) {
        System.out.println("[AttendTCPClientsThread]I'll try to remove: " + this.rootDirectory + File.separator + this.getClientWorkingDir() + newDirName);
        File file = new File(this.rootDirectory + File.separator + this.getClientWorkingDir() + newDirName);
        return file.delete();
    }

    private boolean copyAndPaste(String originalFilePath) {
        try {
            originalFilePath = originalFilePath.replace(("remote" + this.myAddress.getName() + File.separator), this.rootDirectory + File.separator + this.clientRootDir);
            String fileName = new File(originalFilePath).getName();
            System.out.println("[AttendTCPClientsThread]I'll try to copy the file '" + fileName + "' from: " + new File(originalFilePath).toPath());
            System.out.println("[AttendTCPClientsThread]I'll try to paste the file '" + fileName + "' on: " + new File(this.rootDirectory + File.separator + this.getClientWorkingDir()+ fileName).toPath());
            Files.copy(new File(originalFilePath).toPath(), new File(this.rootDirectory + File.separator + this.getClientWorkingDir() + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            return false;
        }
        
        return true;
    }

    private byte[] downloadFile(String originalFilePath) throws IOException {
        originalFilePath = originalFilePath.replace(("remote" + this.myAddress.getName() + File.separator), this.rootDirectory + File.separator + this.clientRootDir);
        return Files.readAllBytes(new File(originalFilePath).toPath());
    }

    private boolean uploadFile(byte[] fileContent, String fileName) {
        FileOutputStream localFileOutputStream = null;
        try {
            System.out.println("[AttendTCPClientsThread]I'll try to create the file: " + this.rootDirectory + File.separator + this.clientWorkingDir + fileName);
            localFileOutputStream = new FileOutputStream(this.rootDirectory + File.separator + this.clientWorkingDir + fileName);
            localFileOutputStream.write(fileContent);
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            try {
                if(localFileOutputStream != null)
                    localFileOutputStream.close();
            } catch (IOException ex) {
                return false;
            }
        }
    }
}
