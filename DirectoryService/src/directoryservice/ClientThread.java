
package directoryservice;

import DataMessaging.DataAddress;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */
public class ClientThread extends Thread {
    public static final int TIMEOUT = 30000; // 30 segundos timeout
    List<DataAddress> list;
    DatagramPacket packet;
    DatagramSocket socket;
    ByteArrayOutputStream bOut;
    ObjectOutputStream out;
    
    public ClientThread(List<DataAddress> list, String message, DatagramSocket socket, DatagramPacket packet) {
        this.list = list;
        this.packet = packet;
        this.socket = socket;
    }
    
    private <T> void sendMessage(T message) {
        try {
            bOut = new ByteArrayOutputStream(1000);
            out = new ObjectOutputStream(bOut);
            out.writeObject(message);

            packet.setData(bOut.toByteArray());
            packet.setLength(bOut.size());

            socket.send(packet);
         } catch (IOException ex) {
            System.out.println("<DirectoryService> " + ex);
        }
    }
    
    @Override
    public void run() {
        
    }
}
