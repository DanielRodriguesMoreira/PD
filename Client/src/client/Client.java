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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class Client implements Constants {
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

    public void sendMessageToServiceDirectory(String tipoPedidoAExecutar) {
        try {
            serverDirectoryAddr = InetAddress.getByName(this.directoryServiceIP);
            serverDirectoryPort = Integer.parseInt(this.directoryServicePort);   

            bOut = new ByteArrayOutputStream();            
            out = new ObjectOutputStream(bOut);

            // <editor-fold defaultstate="collapsed" desc=" Criar o datagramAddress para enviar para o Service directory ">
            message = new ClientMessage(dataAddress, null, null, tipoPedidoAExecutar, null, null, false);
            // </editor-fold>
           
            out.writeObject(message);
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
}
