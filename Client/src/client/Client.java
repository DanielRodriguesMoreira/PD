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
/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class Client extends Observable implements Constants {
    String username;
    String directoryServiceIP;
    String directoryServicePort;
    DatagramSocket dataSocket = null;
    DataAddress dataAddress = null;

    ByteArrayOutputStream bOut = null;
    ObjectOutputStream out = null;
    ObjectInputStream in;
    DatagramPacket packet;
    ClientMessage message;

    InetAddress serverDirectoryAddr;
    int serverDirectoryPort;
    List<DataAddress> OnlineServers;
    List<DataAddress> OnlineClients;
    
    Socket socketTCP = null;
    ObjectOutputStream outTCP = null;
    ObjectInputStream inTCP = null;
        
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
    }    

    public void sendMessageToServiceDirectory(DataAddress usernameToSend, String message){
        this.sendMessageToServiceDirectory(CLIENT_SENDMESSAGE, usernameToSend, message);
    }
    public void sendMessageToServiceDirectory(String tipoPedidoAExecutar) {
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
    
    public void receiveMessage() {
        try {
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
        } catch (IOException ex) {
            System.out.println("Error trying to create a ObjectInputStream\n");
        } catch (ClassNotFoundException ex) {
            System.out.println("Class not found\n");
        }
    }
    
    public boolean checkClientExists() {
        sendMessageToServiceDirectory(CLIENT_MSG_CHECK_USERNAME);
        receiveMessage();
        return message.isExists();
    }
    
    public void createHeartbeatThread() {
        Thread t1 = new ImAliveThread(dataSocket, serverDirectoryAddr, serverDirectoryPort, dataAddress);
        t1.start();
    }

    public List<DataAddress> getOnlineServers() {
        return this.OnlineServers;
    }
    
    public List<DataAddress> getOnlineClients() {
        return this.OnlineClients;
    }

    public String getMessage() {
        System.out.println("Estou dentro do getMessage()\n");
        this.message.setMessage("OLA daniel\n");
        return this.message.getMessage();
    }
    
    public boolean connectServer(DataAddress serverAddress) {

        try {
            this.prepareSocketTCP(serverAddress);
            this.sendMessageToServer(new String("OLA"));
            
            String resposta = (String)inTCP.readObject();
            System.out.println(resposta);
            
            return true;
        } catch (IOException ex) {
            System.err.println("An error occurred in accessing the socket:\n\t" + ex);
            return false;
        } catch (ClassNotFoundException ex) {
            System.err.println(ex);
            return false;
        }
        
    }
    
    private void prepareSocketTCP(DataAddress serverAddress) throws IOException{
        
        this.socketTCP = new Socket(serverAddress.getIp(), serverAddress.getPort());
        
        inTCP = new ObjectInputStream(this.socketTCP.getInputStream());
        outTCP = new ObjectOutputStream(this.socketTCP.getOutputStream());
        
    }
    
    private void sendMessageToServer(String tipoPedidoAExecutar){
        
        try {
            outTCP.writeUnshared(new String("OLA"));
            outTCP.flush();
            
            //String response = (String)in.readObject();
            
        } catch (IOException ex) {
            System.err.println("An error occurred in accessing the socket:\n\t" + ex);
        }
    }
}
