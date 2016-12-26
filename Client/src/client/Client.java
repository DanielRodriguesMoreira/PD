package client;

import Constants.Constants;
import DataMessaging.ClientMessage;
import DataMessaging.DataAddress;
import Threads.ImAliveThread;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class Client extends Observable implements Constants, Runnable {
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
    
    private Socket socketTCP = null;
    private ObjectOutputStream outTCP = null;
    private ObjectInputStream inTCP = null;
            
    public Client (String username, String directoryServiceIP, String directoryServicePort) {
        this.username = username;
        this.directoryServiceIP = directoryServiceIP;
        this.directoryServicePort = directoryServicePort;
        try {
            this.dataSocket = new DatagramSocket();
            this.dataAddress = new DataAddress(username, InetAddress.getLocalHost(), dataSocket.getLocalPort(), -1);
        } catch (SocketException | UnknownHostException ex) {
            System.out.println("Error trying to create the DatagramSocket\n");
        }
        
        // <editor-fold defaultstate="collapsed" desc=" Create thread to receive on UDP Socket ">
        Runnable run = this;
        Thread threadToReceive = new Thread(run);
        threadToReceive.start();
        // </editor-fold>
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
            System.out.println("DirectoryServiceIP/Port IOException\n");
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Methods used by interface (ClientGUI) ">
    public boolean checkClientExists() {
        sendMessageToServiceDirectory(CLIENT_MSG_CHECK_USERNAME);
        boolean exists = message.isExists();
        System.out.println("Existe = " + exists);
        if(exists){
            return true;
        } else {
            Thread t1 = new ImAliveThread(dataSocket, serverDirectoryAddr, serverDirectoryPort, dataAddress);
            t1.start();
            return false;
        }
    }
    
    public void getAllLists(){
        sendMessageToServiceDirectory(CLIENT_GET_ALL_LISTS);
    }
    
    public void sendMessageTo(DataAddress clientToSend, String messageToSend){
        this.sendMessageToServiceDirectory(clientToSend, messageToSend);
    }
    
    public void sendMessageToAll(String messageToSend){
        this.sendMessageToServiceDirectory(null, messageToSend);
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
    
    public void connectoToServer(DataAddress serverToConnect){
        System.out.println(serverToConnect.getIp().getHostName());
        
        try {
            this.socketTCP = new Socket(serverToConnect.getIp(), serverToConnect.getPort());
            this.inTCP = new ObjectInputStream(this.socketTCP.getInputStream());
            this.outTCP = new ObjectOutputStream(this.socketTCP.getOutputStream());
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    // </editor-fold>
    
    @Override
    public void run() {
        try {
            while(true){
                packet = new DatagramPacket(new byte[DATAGRAM_MAX_SIZE], DATAGRAM_MAX_SIZE);
                dataSocket.receive(packet);

                in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));

                System.out.println("<Client> Packet received");
                message = (ClientMessage) in.readObject();

                switch(message.getRequest()) {
                    // <editor-fold defaultstate="collapsed" desc=" CLIENT_GET_ALL_LISTS ">
                    case CLIENT_GET_ALL_LISTS:
                        this.OnlineServers = message.getListServers();
                        this.OnlineClients = message.getListClients();
                        break;
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc=" CLIENT_MSG_CHECK_USERNAME ">
                    case CLIENT_MSG_CHECK_USERNAME:
                        this.OnlineServers = message.getListServers();
                        this.OnlineClients = message.getListClients();
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
            }
        } catch (IOException ex) {
            System.out.println("Error trying to create a ObjectInputStream\n");
        } catch (ClassNotFoundException ex) {
            System.out.println("Class not found\n");
        }
    }
}
