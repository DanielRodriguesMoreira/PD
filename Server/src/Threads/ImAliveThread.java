
package Threads;

import java.net.DatagramSocket;
import java.net.InetAddress;
import DataMessaging.DataAddress;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ImAliveThread extends Thread{

    public static final int HEARTBEAT = 30 * 1000; //30 segundos
    String serverName;
    InetAddress serverIP;
    int serverPort;
    DatagramSocket socket;
    InetAddress serviceDirectoryAddress;
    int serviceDirectoryPort;
    DataAddress dataAddress;
    DatagramPacket packetToSend;
    
    public ImAliveThread(DatagramSocket socket, InetAddress serviceDirectoryAddress, 
            int serviceDirectoryPort, DataAddress serverAdress){
        
        // <editor-fold defaultstate="collapsed" desc=" Initialize variables ">
        this.socket                     = socket;
        this.serviceDirectoryAddress    = serviceDirectoryAddress;
        this.serviceDirectoryPort       = serviceDirectoryPort;
        this.serverName                 = serverAdress.getName();
        this.serverIP                   = serverAdress.getIP();
        this.serverPort                 = serverAdress.getPort();
        // </editor-fold>
    }
    
    @Override
    public void run(){
        try {
                // <editor-fold defaultstate="collapsed" desc=" Create DataAddress object and write it on OutputStream ">
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();            
                ObjectOutputStream out = new ObjectOutputStream(bOut);
                
                dataAddress = new DataAddress(serverName, serverIP, serverPort);
                
                out.writeUnshared(dataAddress);
                out.flush();
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Create and setup packetToSend ">
                packetToSend = new DatagramPacket(bOut.toByteArray(), bOut.size());
                packetToSend.setAddress(serviceDirectoryAddress);
                packetToSend.setPort(serviceDirectoryPort);
                // </editor-fold>
            
                while(true) {
                    socket.send(packetToSend);
                    // <editor-fold defaultstate="collapsed" desc=" This is just a test ">
                        System.out.println("Nome : " + serverName);
                        System.out.println("IP : " + serverIP.getHostAddress());
                        System.out.println("Port : " + serverPort);
                    // </editor-fold>
                    Thread.sleep(HEARTBEAT);
                }
            
        } catch (IOException ex) {
            System.out.println("An error occurred in accessing the socket:\n\t" + ex);
        } catch (InterruptedException ex) {
            System.out.println("ImAliveThread was been interrupted:\n\t" + ex);
        }
    }
}
