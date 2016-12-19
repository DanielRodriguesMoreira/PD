
package Threads;

import Constants.Constants;
import java.net.DatagramSocket;
import java.net.InetAddress;
import DataMessaging.DataAddress;
import DataMessaging.ServerMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ImAliveThread extends Thread implements Constants{

    DatagramSocket socket = null;
    InetAddress serviceDirectoryAddress = null;
    int serviceDirectoryPort = -1;
    DataAddress dataAddress = null;
    DatagramPacket packetToSend = null;
    ServerMessage serverMessage = null;
    
    public ImAliveThread(DatagramSocket socket, InetAddress serviceDirectoryAddress, 
            int serviceDirectoryPort, DataAddress serverAdress){
        
        // <editor-fold defaultstate="collapsed" desc=" Initialize variables ">
        this.socket                     = socket;
        this.serviceDirectoryAddress    = serviceDirectoryAddress;
        this.serviceDirectoryPort       = serviceDirectoryPort;
        this.dataAddress                = serverAdress;
        // </editor-fold>
    }
    
    @Override
    public void run(){
        try {
                // <editor-fold defaultstate="collapsed" desc=" Create ServerMessage object and write it on OutputStream ">
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();            
                ObjectOutputStream out = new ObjectOutputStream(bOut);
                
                this.serverMessage = new ServerMessage(dataAddress, null, SERVER_MSG_HEARTBEAT, false);
                
                out.writeUnshared(serverMessage);
                out.flush();
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Create and setup packetToSend ">
                packetToSend = new DatagramPacket(bOut.toByteArray(), bOut.size());
                packetToSend.setAddress(serviceDirectoryAddress);
                packetToSend.setPort(serviceDirectoryPort);
                // </editor-fold>
            
                while(true) {
                    socket.send(packetToSend);
                    Thread.sleep(HEARTBEAT);
                }
            
        } catch (IOException ex) {
            System.out.println("[ImAliveThread]An error occurred in accessing the socket:\n\t" + ex);
        } catch (InterruptedException ex) {
            System.out.println("ImAliveThread was been interrupted:\n\t" + ex);
        }
    }
}
