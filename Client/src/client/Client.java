package client;

import Constants.ClientServerRequests;
import DataMessaging.Login;
import Constants.Constants;
import DataMessaging.ClientMessage;
import DataMessaging.ClientServerMessage;
import DataMessaging.DataAddress;
import Exceptions.ClientNotLoggedInException;
import Exceptions.CreateAccountException;
import Exceptions.ServerConnectionException;
import Exceptions.UsernameOrPasswordIncorrectException;
import Threads.ImAliveThread;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;


public class Client extends Observable implements Constants, FilesInterface, ClientServerRequests, Runnable {
    
    // <editor-fold defaultstate="collapsed" desc=" Variables declaration ">
    private String username;
    private String directoryServiceIP;
    private String directoryServicePort;
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
    // </editor-fold>
    
    private Map<DataAddress, Socket> serversMap = null;
            
    public Client (String username, String directoryServiceIP, String directoryServicePort) {
        this.username = username;
        this.directoryServiceIP = directoryServiceIP;
        this.directoryServicePort = directoryServicePort;
        this.serversMap = new HashMap<>();
        try {
            this.dataSocket = new DatagramSocket();
            this.dataAddress = new DataAddress(username, InetAddress.getLocalHost(), dataSocket.getLocalPort(), -1);
        } catch (SocketException ex) {
            System.err.println("An error occurred with the UDP socket level:\n\t" + ex);
        } catch (UnknownHostException ex) {
            System.err.println("Can't find directory service");
        }
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
            
            System.out.println("<Client> Message Sended\n");
        } catch (IOException ex) {
            System.err.println("DirectoryServiceIP/Port IOException\n" + ex);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Methods used by interface (ClientGUI) ">
    public boolean checkClientExists() {
        
        sendMessageToServiceDirectory(CLIENT_MSG_CHECK_USERNAME);
       // this.receiveMessageFromServiceDirectory();
        boolean exists = message.isExists();
        System.out.println("Exists = " + exists);
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
    public void Login(Login login, DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, CreateAccountException{
        ClientServerMessage message = new ClientServerMessage(login, true, dataAddress);
        sendMessageToServer(message, serverToSend);
    }

    @Override
    public void Logout(Login login, DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, CreateAccountException{
        ClientServerMessage message = new ClientServerMessage(login, false, dataAddress);
        sendMessageToServer(message, serverToSend);
    }

    @Override
    public void CreateAccount(Login login, DataAddress serverToSend) 
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, CreateAccountException{
        ClientServerMessage message = new ClientServerMessage(login, dataAddress);
        sendMessageToServer(message, serverToSend);
    }
    
    @Override
    public File[] GetWorkingDirContent(DataAddress serverToSend)
            throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, CreateAccountException{
        ClientServerMessage message = new ClientServerMessage(dataAddress, true, false);
        message = sendMessageToServer(message, serverToSend);
        return message.getWorkingDirContent();
    }

    @Override
    public String GetWorkingDirPath(DataAddress serverToSend) throws ServerConnectionException, UsernameOrPasswordIncorrectException, ClientNotLoggedInException, CreateAccountException {
        ClientServerMessage message = new ClientServerMessage(dataAddress, false, true);
        message = sendMessageToServer(message, serverToSend);
        return message.getWorkingDirectoryPath();
    }
    
    @Override
    public boolean GetFilesInDirectory(File directory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            
            System.out.println("<Client> Packet received");
            message = (ClientMessage) input.readObject();
            System.out.println("Recebi a resposta de " + message.getRequest());
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
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex);
        }
    }

    private ClientServerMessage sendMessageToServer(ClientServerMessage message, DataAddress serverToSend) 
            throws UsernameOrPasswordIncorrectException, ServerConnectionException, ClientNotLoggedInException, CreateAccountException{
        
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
                    if(message.getSuccess() != true) throw new UsernameOrPasswordIncorrectException();
                    break;
                case LOGOUT:
                    if(message.getSuccess() != true) throw new ClientNotLoggedInException();
                    break;
                case CREATE_ACCOUNT:
                    if(message.getSuccess() != true) throw new CreateAccountException();
                    break;
            }
            return message;
        } catch (IOException | ClassNotFoundException ex) {
            throw new ServerConnectionException("Error with Server connection!\n(" + ex + ")");
        }
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
}
