
package server;

import Constants.Constants;
import Constants.ServerRequestsConstants;
import Threads.ImAliveThread;
import DataMessaging.DataAddress;
import DataMessaging.ServerMessage;
import Exceptions.ServerAlreadyExistsException;
import Threads.AttendTCPClientsThread;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class Server implements Constants, Runnable, ServerRequestsConstants{

    private static final int TIMEOUT = 10; //segundos
    private ServerSocket serverSocketTCP = null;
    private Socket toClientSocket = null;
    private InetAddress directoryServiceIP = null;
    private int directoryServicePort = -1;
    private DataAddress myAddress = null;
    private List<DataAddress> usersLoggedIn = null;
    
    public Server(ServerSocket serverSocket, InetAddress dsIP, int dsPort, DataAddress myTCPAddress){
        this.serverSocketTCP = serverSocket;
        this.directoryServiceIP = dsIP;
        this.directoryServicePort = dsPort;
        this.myAddress = myTCPAddress;
        this.usersLoggedIn = Collections.synchronizedList(new ArrayList<DataAddress>());
    }
    
    public static void main(String[] args) {
        
        String serverName = null;
        InetAddress directoryServiceAddress = null;
        int directoryServicePort = -1;
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        ByteArrayOutputStream bOut = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        ServerSocket serverSocket = null;
        int socketTCPPort = -1;
        String localRequest = null;

        if(args.length != 3){
            System.out.println("Sintaxe: java Server <name> <DirectoryServiceAddress> <DirectoryServicePort>");
            return;
        }
        
        try {
            
            // <editor-fold defaultstate="collapsed" desc=" Fill serverName and directoryService Adress ">
            serverName = args[0];
            directoryServiceAddress = InetAddress.getByName(args[1]);
            directoryServicePort = Integer.parseInt(args[2]);
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" Create UDP socket ">
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT*1000);
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" Create TCP Socket ">
            serverSocket = new ServerSocket(0);
            socketTCPPort = serverSocket.getLocalPort();
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" Create ServerMessage and send it to Directory Service (by UDP) ">
            
            bOut = new ByteArrayOutputStream();            
            out = new ObjectOutputStream(bOut);
            
            // Create DataAddress object with serverName, serverAddress and serverPort TCP
            DataAddress myTCPAddress = new DataAddress(serverName, InetAddress.getLocalHost(), socketTCPPort, -1);
            ServerMessage serverMessage = new ServerMessage(myTCPAddress, null, SERVER_MSG_CHECK_USERNAME, false);

            out.writeObject(serverMessage);
            out.flush();
            
            packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), directoryServiceAddress, directoryServicePort);
            socket.send(packet);
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" Receive ServerMessage from Directory Service (by UDP) ">
            do{
            
                packet = new DatagramPacket(new byte[DATAGRAM_MAX_SIZE], DATAGRAM_MAX_SIZE);
                socket.receive(packet);
                
                in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));                
                serverMessage = (ServerMessage)in.readObject();
            }while(!packet.getAddress().equals(directoryServiceAddress));
            // </editor-fold>
            
            checkIfServerAlreadyExists(serverMessage);

            // <editor-fold defaultstate="collapsed" desc=" Create and start ImAliveThread ">
            Thread threadImAlive = new ImAliveThread(socket, directoryServiceAddress, 
                    directoryServicePort, myTCPAddress);
            threadImAlive.start();
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc=" Create and start Accept Clients Thread (this) ">
            Runnable run = new Server(serverSocket, directoryServiceAddress, directoryServicePort, myTCPAddress);
            Thread threadAcceptClients = new Thread(run);
            threadAcceptClients.start();
            // </editor-fold>
            
            BufferedReader bufferReaderIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Command: ");
            while(true){
                localRequest = bufferReaderIn.readLine();
                switch(localRequest.toUpperCase()){
                    case SERVER_GET_REQUEST_LIST:
                        showCommandList();
                        break;
                    case SERVER_GET_USERNAME: 
                        System.out.println(serverName); 
                        break;
                    case SERVER_GET_DIRECTORYSERVICE_ADDRESS:
                        System.out.println("Directory Service address: " + directoryServiceAddress +
                                ":" + directoryServicePort);
                        break;
                    case SERVER_GET_MYADDRESS:
                        System.out.println("My address: " + InetAddress.getLocalHost());
                        System.out.println("UDP port: " + socket.getLocalPort());
                        System.out.println("TCP port: " + socketTCPPort);
                        break;
                    default:
                        System.out.println("Command not found.");
                }
             System.out.println("\nCommand: ");   
            }
            
        } catch(ServerAlreadyExistsException ex) {
            System.out.println(ex.getError());
        } catch(UnknownHostException ex) {
            System.out.println("Can't find directory service " + serverName);
        } catch(NumberFormatException e){
            System.out.println("The server port must be a positive integer.");
        } catch(SocketTimeoutException e){
            System.out.println("Não foi recebida qualquer resposta:\n\t"+e);
        } catch(SocketException ex) {
            System.out.println("An error occurred with the UDP socket level:\n\t" + ex);
        } catch(IOException ex) {
            System.out.println("An error occurred in accessing the socket:\n\t" + ex);
        } catch(ClassNotFoundException ex) {
            System.out.println("The object received is not the expected type:\n\t" + ex);
        }
    }
    
    private static void checkIfServerAlreadyExists(ServerMessage serverMessage) throws ServerAlreadyExistsException{
        if(serverMessage.getExists()) 
            throw new ServerAlreadyExistsException(serverMessage.getServerName());
    }
    
    private static void showCommandList(){
        System.out.println("Commands list:");
        System.out.println("GetUsername - Get server username");
        System.out.println("GetDirectoryServiceAddress - Get directory service address");
        System.out.println("GetMyAddress - Get server address");
        System.out.println("GetList - Get commands list");
    }
    
    @Override
    public void run() {
        
         while(true){     
             try {
                //Accept Client
                this.toClientSocket = this.serverSocketTCP.accept();
                
                //Start thread to attend the client
                Thread attendClientThread = new AttendTCPClientsThread(this.toClientSocket, this.myAddress,
                this.directoryServiceIP, this.directoryServicePort, this.usersLoggedIn);
                attendClientThread.start();
                
            } catch (IOException ex) {
                System.out.println("An error occurred in accessing the socket:\n\t" + ex);
            }
        }
    } 
}
