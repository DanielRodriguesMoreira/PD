

import Constants.ClientServerRequests;
import Constants.Constants;
import Exceptions.ClientNotLoggedInException;
import Exceptions.CopyFileException;
import Exceptions.CreateAccountException;
import Exceptions.GetFileContentException;
import Exceptions.MakeDirException;
import Exceptions.RemoveFileOrDirException;
import Exceptions.ServerConnectionException;
import Exceptions.UploadException;
import Exceptions.UsernameOrPasswordIncorrectException;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Observable implements Constants, FilesInterface, ClientServerRequests, Runnable {
    
    // <editor-fold defaultstate="collapsed" desc=" Variables declaration ">
    private String username;
    private String directoryServiceIP;
    private String directoryServicePort;
    private String homePath;
    private DatagramSocket dataSocket = null;
    private DataAddress dataAddress = null;

    private ByteArrayOutputStream bOut = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in;
    private DatagramPacket packet;
    private ClientMessage message;

    private InetAddress serverDirectoryAddr;
    private int serverDirectoryPort;
    private List<DataAddress> OnlineServers;
    private List<DataAddress> OnlineClients;
    private byte[] fileContent;
    private String fileName;
    
    private boolean isLoggedIn = false;
    private List<String> serversWhereImNotAuthenticated;
    // </editor-fold>
    
    private Map<DataAddress, Socket> serversMap = null;
            
    public Client (String username, String directoryServiceIP, String directoryServicePort) {
        this.username = username;
        this.directoryServiceIP = directoryServiceIP;
        this.directoryServicePort = directoryServicePort;
        this.serversMap = new HashMap<>();
        this.homePath = "";
        try {
            this.dataSocket = new DatagramSocket();
            this.dataSocket.setSoTimeout(HEARTBEAT+5000);
            this.dataAddress = new DataAddress(username, InetAddress.getLocalHost(), dataSocket.getLocalPort(), -1);
            // <editor-fold defaultstate="collapsed" desc=" REMOTE ">
            ClientRemote clientRemote = new ClientRemote();
            String url = "rmi://" + directoryServiceIP + "/RemoteGetServers";
            RemoteGetServersInterface serviceRemote = (RemoteGetServersInterface)Naming.lookup(url);
            serviceRemote.addClientObserver(clientRemote, dataAddress);
            // </editor-fold>
        } catch (SocketException ex) {
            System.err.println("[Client]An error occurred with the UDP socket level:\n\t" + ex);
            this.exit(true);
        } catch (UnknownHostException ex) {
            System.err.println("[Client]Can't find directory service");
            this.exit(true);
        } catch (RemoteException | NotBoundException | MalformedURLException ex) {
            System.err.println("[Client-Remote] " + ex);
            this.exit(true);
        }
    }
    
    private DataAddress findServerByName(String server) {
        for(DataAddress i : this.OnlineServers) {
            if (i.getName().equals(server))
                return i;
        }
        return null;
    }

    private void sendMessageToServiceDirectory(DataAddress usernameToSend, String message){
        this.sendMessageToServiceDirectory(CLIENT_SENDMESSAGE, usernameToSend, message);
    }
    
    private void sendMessageToServiceDirectory(String tipoPedidoAExecutar) {
        this.sendMessageToServiceDirectory(tipoPedidoAExecutar, null, null);
    }
    
    private void sendMessageToServiceDirectory(String tipoPedidoAExecutar, DataAddress usernameToSend, String messageToSend){
        try {
            serverDirectoryAddr = InetAddress.getByName(this.directoryServiceIP);
            serverDirectoryPort = Integer.parseInt(this.directoryServicePort);   

            bOut = new ByteArrayOutputStream();            
            out = new ObjectOutputStream(bOut);
            
            // <editor-fold defaultstate="collapsed" desc=" Criar o datagramAddress para enviar para o Service directory ">
            this.message = new ClientMessage(dataAddress, usernameToSend, messageToSend, tipoPedidoAExecutar, null, null, false);
            // </editor-fold>
           
            out.writeObject(this.message);
            out.flush();

            packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), serverDirectoryAddr, serverDirectoryPort);
            dataSocket.send(packet);
            
            System.out.println("[Client]Message Sended\n");
        } catch (IOException ex) {
            System.err.println("[Client]DirectoryServiceIP/Port IOException\n" + ex);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Methods used by interface (ClientGUI) ">
    public boolean checkClientExists() {
        
        sendMessageToServiceDirectory(CLIENT_MSG_CHECK_USERNAME);
        this.receiveMessageFromServiceDirectory();
        boolean exists = message.isExists();
        System.out.println("[Client]Exists = " + exists);
        if(exists){
            return true;
        } else {
            Thread t1 = new ImAliveThread(dataSocket, serverDirectoryAddr, serverDirectoryPort, dataAddress);
            t1.start();
            Thread t2 = new Thread(this);
            t2.start();
            return false;
        }
    }
    
    public void getAllLists(){
        sendMessageToServiceDirectory(CLIENT_GET_ALL_LISTS);
        //this.receiveMessageFromServiceDirectory();
    }
    
    public void sendMessageTo(DataAddress clientToSend, String messageToSend){
        this.sendMessageToServiceDirectory(clientToSend, messageToSend);
        setChanged();
        notifyObservers();
    }
    
    public void sendMessageToAll(String messageToSend){
        this.sendMessageToServiceDirectory(null, messageToSend);
        setChanged();
        notifyObservers();
    }
    
    public List<DataAddress> getOnlineServers() {
        return this.OnlineServers;
    }
    
    public List<DataAddress> getOnlineClients() {
        return this.OnlineClients;
    }

    public String getMessage() {
        return this.message.getMessage();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" FilesInterface ">
    @Override
    public void Login(String password, DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        ClientServerMessage message = new ClientServerMessage(password, true, dataAddress);
        sendMessageToServer(message, serverToSend);
    }

    @Override
    public void Logout(DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        ClientServerMessage message = new ClientServerMessage(null, false, dataAddress);
        sendMessageToServer(message, serverToSend);
    }

    @Override
    public void CreateAccount(Login login, DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        ClientServerMessage message = new ClientServerMessage(login, dataAddress);
        sendMessageToServer(message, serverToSend);
    }
    
    @Override
    public ArrayList<File> GetWorkingDirContent(DataAddress serverToSend)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        ClientServerMessage message = new ClientServerMessage(dataAddress, true);
        message = sendMessageToServer(message, serverToSend);
        return message.getWorkingDirContent();
    }
    
    @Override
    public ArrayList<File> ChangeDirectory(String serverName, String dirName) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        if (!serverName.equals("C:\\")){
            DataAddress serverToSend = findServerByName(serverName);
            if(serverToSend == null) throw new ServerConnectionException("Server not found!");
            ClientServerMessage message = new ClientServerMessage(dataAddress, dirName);
            message = sendMessageToServer(message, serverToSend);
            return message.getWorkingDirContent();
        } else {
            if (dirName.contains(homePath)){
                homePath = homePath.replace(homePath.substring(homePath.lastIndexOf(File.separator),homePath.length()),"");
                if (homePath.equals("C:"))
                    homePath+=File.separator;
             }else{
                if (!(homePath.charAt(homePath.length()-1) == File.separatorChar))
                    homePath += File.separator;
                homePath += dirName;
            }
            File f = new File(homePath);
            if (f.listFiles() != null)
                return new ArrayList<>(Arrays.asList(f.listFiles()));
            return null;
        }
    }

    @Override
    public String GetWorkingDirPath(String serverName) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        if (!serverName.equals("C:\\")){
            DataAddress serverToSend = findServerByName(serverName);
            if (serverToSend == null) throw new ServerConnectionException("Server not found!");
            ClientServerMessage message = new ClientServerMessage(dataAddress, false);
            message = sendMessageToServer(message, serverToSend);
            return message.getWorkingDirectoryPath();
        } else {
            return homePath + File.separator;
        }
    }
    
    @Override
    public ArrayList<File> MakeDir(String serverName, String newDirName) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        if (!serverName.equals("C:\\")){
            DataAddress serverToSend = findServerByName(serverName);
            if(serverToSend == null) throw new ServerConnectionException("Server not found!");
            ClientServerMessage message = new ClientServerMessage(dataAddress, newDirName, true);
            message = sendMessageToServer(message, serverToSend);
            return message.getWorkingDirContent();
        } else {
            File file = new File(homePath + File.separator + newDirName);
            if(!file.mkdir()) throw new MakeDirException();
            return new ArrayList<>(Arrays.asList((new File(homePath)).listFiles()));
        }
    }
    
    @Override
    public ArrayList<File> Remove(String serverName, String fileOrDirName) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        if (!serverName.equals("C:\\")){
            DataAddress serverToSend = findServerByName(serverName);
            if(serverToSend == null) throw new ServerConnectionException("Server not found!");
            ClientServerMessage message = new ClientServerMessage(dataAddress, fileOrDirName, false);
            message = sendMessageToServer(message, serverToSend);
            return message.getWorkingDirContent();
        } else {
            File file = new File(homePath + File.separator + fileOrDirName);
            if(!file.delete()) throw new RemoveFileOrDirException();
            return new ArrayList<>(Arrays.asList((new File(homePath)).listFiles()));
        }
    }
    
    @Override
    public ArrayList<File> CopyAndPaste(String serverName, String originalFilePath) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        if (!serverName.equals("C:\\")){
            DataAddress serverToSend = findServerByName(serverName);
            if(serverToSend == null) throw new ServerConnectionException("Server not found!");
            ClientServerMessage message = new ClientServerMessage(dataAddress, originalFilePath, COPY_AND_PASTE);
            message = sendMessageToServer(message, serverToSend);
            return message.getWorkingDirContent();
        } else {
            String fileName = originalFilePath.substring(originalFilePath.lastIndexOf(File.separator)+1, originalFilePath.length());
            try {
                Files.copy(new File(originalFilePath).toPath(), new File(homePath + File.separator + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                return new ArrayList<>(Arrays.asList((new File(homePath)).listFiles()));
            } catch (IOException ex) {
                throw new CopyFileException(fileName);
            }
        }
    }
    
    @Override
    public void Download(String serverName, String originalFilePath) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        if (!serverName.equals("C:\\")){
            DataAddress serverToSend = findServerByName(serverName);
            if(serverToSend == null) throw new ServerConnectionException("Server not found!");
            ClientServerMessage message = new ClientServerMessage(dataAddress, originalFilePath, DOWNLOAD);
            message = sendMessageToServer(message, serverToSend);
            this.fileName = originalFilePath.substring(originalFilePath.lastIndexOf(File.separator)+1, originalFilePath.length());
            this.fileContent = message.getFileContent();
        } else {
            try {
                this.fileName = originalFilePath.substring(originalFilePath.lastIndexOf(File.separator)+1, originalFilePath.length());
                this.fileContent = Files.readAllBytes(new File(originalFilePath).toPath());
            } catch (IOException ex) {
                throw new GetFileContentException();
            }
        }
    }
    
        @Override
    public ArrayList<File> Upload(String serverName) throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        if (!serverName.equals("C:\\")){
            DataAddress serverToSend = findServerByName(serverName);
            if(serverToSend == null) throw new ServerConnectionException("Server not found!");
            ClientServerMessage message = new ClientServerMessage(dataAddress, this.fileContent, this.fileName);
            message = sendMessageToServer(message, serverToSend);
            return message.getWorkingDirContent();
        } else {
            FileOutputStream localFileOutputStream = null;
            try {
                localFileOutputStream = new FileOutputStream(homePath + File.separator + fileName);
                localFileOutputStream.write(fileContent);
            } catch (FileNotFoundException ex) {
                throw new UploadException();
            } catch (IOException ex) {
                throw new UploadException();
            } finally {
                try {
                    if(localFileOutputStream != null)
                        localFileOutputStream.close();
                } catch (IOException ex) {
                    throw new UploadException();
                }
            }
            return new ArrayList<>(Arrays.asList((new File(homePath)).listFiles()));
        }
    }
    
    @Override
    public void GetFileContent(String serverName, String fileToOpen) throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, 
            GetFileContentException, UploadException{
        if (!serverName.equals("C:\\")){
            DataAddress serverToSend = findServerByName(serverName);
            if(serverToSend == null) throw new ServerConnectionException("Server not found!");
            ClientServerMessage message = new ClientServerMessage(dataAddress, fileToOpen, DOWNLOAD);
            message = sendMessageToServer(message, serverToSend);
            this.fileName = fileToOpen.substring(fileToOpen.lastIndexOf(File.separator)+1, fileToOpen.length());
            this.fileContent = message.getFileContent();
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(Files.write(new File(this.fileName).toPath(), this.fileContent).toFile());
            } catch (IOException ex) {
                throw new GetFileContentException();
            }
        } else {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(new File(fileToOpen));
                //desktop.open(new File((homePath + File.separator + fileToOpen).replace(File.separator + File.separator, File.separator)));
            } catch (IOException ex) {
                throw new GetFileContentException();
            }
        }
    }
    // </editor-fold>
    
    // </editor-fold>
    
    private void createSocket(DataAddress serverToConnect) throws IOException {
        if(!this.socketAlreadyExists(serverToConnect)){
            Socket socket = new Socket(serverToConnect.getIp(), serverToConnect.getPort());
            this.serversMap.put(serverToConnect, socket);
        }
    }
    
    private boolean socketAlreadyExists(DataAddress serverToConnect){
        for(DataAddress dataAddress : this.serversMap.keySet()){
            if(dataAddress.equals(serverToConnect))
                return true;
        }
        return false;
    }
    
    private void receiveMessageFromServiceDirectory(){
        try {
            DatagramPacket packetGram = new DatagramPacket(new byte[DATAGRAM_MAX_SIZE], DATAGRAM_MAX_SIZE);
            dataSocket.receive(packetGram);
            
            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(packetGram.getData(), 0, packetGram.getLength()));
            
            System.out.println("[Client]Packet received");
            message = (ClientMessage) input.readObject();
            System.out.println("[Client]Request received" + message.getRequest());
            switch(message.getRequest()) {
                // <editor-fold defaultstate="collapsed" desc=" CLIENT_GET_ALL_LISTS ">
                case CLIENT_GET_ALL_LISTS:
                this.OnlineServers = message.getListServers();
                this.OnlineClients = message.getListClients();
                break;
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" CLIENT_MSG_CHECK_USERNAME ">
            case CLIENT_MSG_CHECK_USERNAME:
                //this.OnlineServers = message.getListServers();
                //this.OnlineClients = message.getListClients();
                break;
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" CLIENT_GET_ONLINE_SERVERS ">
            case CLIENT_GET_ONLINE_SERVERS:
                this.OnlineServers = message.getListServers();
                break;
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" CLIENT_GET_ONLINE_CLIENTS ">
                case CLIENT_GET_ONLINE_CLIENTS:
                    this.OnlineClients = message.getListClients();
                    break;
                    // </editor-fold>
            }
            setChanged();
            notifyObservers();
        } catch(SocketTimeoutException ex){
            if(message.getRequest().equals(CLIENT_MSG_CHECK_USERNAME)){
                System.err.println("[Client]Timeout exceeded!");
                this.exit(true);
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("[Client]" + ex);
            if(message.getRequest().equals(CLIENT_MSG_CHECK_USERNAME))
                this.exit(true);
        }
    }

    private ClientServerMessage sendMessageToServer(ClientServerMessage message, DataAddress serverToSend) 
            throws UsernameOrPasswordIncorrectException, ServerConnectionException, ClientNotLoggedInException, 
            CreateAccountException, MakeDirException, RemoveFileOrDirException, CopyFileException, GetFileContentException, UploadException{
        
        try {
            this.createSocket(serverToSend);
            Socket socket = this.getServerTCPSocket(serverToSend);
            
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            
            out.writeUnshared(message);
            out.flush();
            message = (ClientServerMessage)in.readObject();
            switch(message.getRequest()){
                case LOGIN: 
                    this.isLoggedIn = message.getSuccess();
                    if(message.getSuccess() != true) throw new UsernameOrPasswordIncorrectException();
                    break;
                case LOGOUT:
                    this.isLoggedIn = !message.getSuccess();
                    if(message.getSuccess() != true) throw new ClientNotLoggedInException();
                    break;
                case CREATE_ACCOUNT:
                    this.isLoggedIn = message.getSuccess();
                    if(message.getSuccess() != true) throw new CreateAccountException();
                    break;
                case MAKE_NEW_DIR:
                    if(message.getSuccess() != true) throw new MakeDirException();
                    break;
                case REMOVE:
                    if(message.getSuccess() != true) throw new RemoveFileOrDirException();
                    break;
                case COPY_AND_PASTE:
                    if(message.getSuccess() != true) throw new CopyFileException(message.getOriginalFilePath());
                    break;
                case DOWNLOAD:
                    if(message.getSuccess() != true) throw new GetFileContentException();
                    break;
                case UPLOAD:
                    if(message.getSuccess() != true) throw new UploadException();
                    break;
            }
            return message;
        } catch (IOException | ClassNotFoundException ex) {
            throw new ServerConnectionException("Error with Server connection!\n(" + ex + ")");
        }
    }
    
    public String getHomePath() {
        return homePath;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }
    
    public boolean isLoggedIn(){
        return this.isLoggedIn;
    }
    
    private Socket getServerTCPSocket(DataAddress server){
        for(DataAddress da : this.serversMap.keySet()){
            if(da.equals(server))
                return this.serversMap.get(da);
        }
        
        return null;
    }

    @Override
    public void run() {
        while(true){
            receiveMessageFromServiceDirectory();
        }
    }

    /**
     * Close the streams (ByteArrayOutput, ObjectInput and ObjectInput)
     * It's not necessary to close the socket because the we close the streams
     * 
     * @param error true if is to return a error, false if isn't
     */
    public void exit(boolean error) {
        try {
            if(bOut != null)
                this.bOut.close();
            if(this.in != null)
                this.in.close();
            if(this.out != null)
                this.out.close();
        } catch(Exception ex){
        } finally{
            if(error)
                System.exit(-1);
            else
                System.exit(0);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Remote ">
    class ClientRemote extends UnicastRemoteObject implements RemoteClientObserverInterface{
        public ClientRemote() throws RemoteException {
            serversWhereImNotAuthenticated = new ArrayList<>();
        }

        @Override
        public void updateServersList(List<String> serversWhereImNotAuthenticated) throws RemoteException {
            serversWhereImNotAuthenticated = new ArrayList<>(serversWhereImNotAuthenticated);
            setChanged();
            notifyObservers();
        }
    }
    
    public List<String> getServersWhereImNotAuthenticated(){
        return serversWhereImNotAuthenticated;
    }
    // </editor-fold>
}
