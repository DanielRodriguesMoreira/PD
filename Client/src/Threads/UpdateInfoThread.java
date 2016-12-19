package Threads;

import DataMessaging.ClientMessage;
import DataMessaging.DataAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class UpdateInfoThread extends Thread {
    DatagramSocket socket = null;
    InetAddress serviceDirectoryAddress = null;
    int serviceDirectoryPort = -1;
    DatagramPacket packetToSend = null;
    ClientMessage clientMessage = null;
    DataAddress dataAddress = null;
    
    public UpdateInfoThread() {
        
    }
    
}
