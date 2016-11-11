package directoryservice;

import DataMessaging.DataAddress;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryService {
    
    public static void main(String[] args) {
        List<DataAddress> serverList;
        //List<DataAddress> clientList;
        DatagramPacket packet;
        DatagramSocket socket = null;
        
        serverList = new ArrayList<>();
        packet = new DatagramPacket(new byte[256], 256);
        try {
            socket = new DatagramSocket(Integer.parseInt(args[0]));
        } catch (SocketException ex) {
            Logger.getLogger(DirectoryService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
     
        
        while(true){
            try {
                socket.receive(packet);
                
            } catch (IOException ex) {
                Logger.getLogger(DirectoryService.class.getName()).log(Level.SEVERE, null, ex);
            }

            
        }
    }
}