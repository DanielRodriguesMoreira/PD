
package server;

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
import java.util.Calendar;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */
public class Server {

    public static final int MAX_SIZE = 10000;
    
    public static void main(String[] args) {
        
        String name = null;
        InetAddress directoryServiceAddress;
        int directoryServicePort;
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        ByteArrayOutputStream bOut = null;
        ObjectOutputStream out = null;
        ObjectInputStream in;
        
        if(args.length != 3){
            System.out.println("Sintaxe: java Server <name> <DirectoryServiceAddress> <DirectoryServicePort>");
            return;
        }
        
        
        try {
            
            name = args[0];
            directoryServiceAddress = InetAddress.getByName(args[1]);
            directoryServicePort = Integer.parseInt(args[2]);
            
            
            socket = new DatagramSocket();
            
            bOut = new ByteArrayOutputStream();            
            out = new ObjectOutputStream(bOut);
            
            out.writeObject(new String(name));
            out.flush();
            
            //
            packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), directoryServiceAddress, directoryServicePort);
            socket.send(packet);
            
            
            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);
            
            in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));                
            //time = (Calendar)in.readObject();
            
            
        } catch (UnknownHostException ex) {
            System.out.println("Can't find directory service " + name);
        } catch (SocketException ex) {
            System.out.println("An error occurred with the UDP socket level:\n\t" + ex);
        } catch (IOException ex) {
            System.out.println("An error occurred in accessing the socket:\n\t" + ex);
        }
        
        
    }

}
