
package Threads;

import Constants.Constants;
import java.net.DatagramSocket;
import java.net.InetAddress;
import DataMessaging.ClientMessage;
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

public class ImAliveThread extends Thread implements Constants{

    DatagramSocket socket = null;
    InetAddress serviceDirectoryAddress = null;
    int serviceDirectoryPort = -1;
    DatagramPacket packetToSend = null;
    ClientMessage clientMessage = null;
    DataAddress dataAddress = null;
    
    public ImAliveThread(DatagramSocket socket, InetAddress serviceDirectoryAddress, 
            int serviceDirectoryPort, DataAddress dataAddress){
        
        // <editor-fold defaultstate="collapsed" desc=" Initialize variables ">
        this.socket                     = socket;
        this.serviceDirectoryAddress    = serviceDirectoryAddress;
        this.serviceDirectoryPort       = serviceDirectoryPort;
        this.dataAddress                = dataAddress;
        // </editor-fold>
    }
    
    @Override
    public void run(){
        try {
                // <editor-fold defaultstate="collapsed" desc=" Create ClientMessage object and write it on OutputStream ">
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();            
                ObjectOutputStream out = new ObjectOutputStream(bOut);
                
                this.clientMessage = new ClientMessage(dataAddress, null, null, IAMALIVE, null, null, false);
                
                out.writeUnshared(clientMessage);
                out.flush();
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Create and setup packetToSend ">
                packetToSend = new DatagramPacket(bOut.toByteArray(), bOut.size());
                packetToSend.setAddress(serviceDirectoryAddress);
                packetToSend.setPort(serviceDirectoryPort);
                // </editor-fold>
            
                while(true) {
                                    
                // <editor-fold defaultstate="collapsed" desc=" This is just a test ">
                        //System.out.println("Nome : " + dataAddress.getName());
                    System.out.println("\nMandei para aqui:");
                        System.out.println("IP : " + serviceDirectoryAddress.getHostAddress());
                        System.out.println("Port : " + serviceDirectoryPort);
                    // </editor-fold>
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
