
package Threads;

import Constants.Constants;
import DataMessaging.DataAddress;
import DataMessaging.ServerMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class AttendTCPClientsThread extends Thread implements Constants{

    private Socket toClientSocket = null;
    private DataAddress myAddress = null;
    private InetAddress directoryServiceIP = null;
    private int directoryServicePort = -1;
    private List<DataAddress> usersLoggedIn = null;
    
    public AttendTCPClientsThread(Socket socket, DataAddress myAddress, InetAddress dsIP, int dsPort, List<DataAddress> users){
        this.toClientSocket = socket;
        this.myAddress = myAddress;
        this.directoryServiceIP = dsIP;
        this.directoryServicePort = dsPort;
        this.usersLoggedIn = users;
    }
    
    @Override
    public void run(){
        
        while(true){
            //Vou recebendo pedidos
            
            //se for x faz x
            //se for y faz y
            //....

        }
        
    }
   
    /**
     * Este método vai ser chamado em 3 situações:
     *      -   Login
     *      -   Logout
     *      -   Criar conta
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
}
