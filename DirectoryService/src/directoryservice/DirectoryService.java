package directoryservice;

import DataMessaging.ConfirmationMessage;
import DataMessaging.DataAddress;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryService {
    
    public static void main(String[] args) {
        List<DataAddress> serverList;
        //List<DataAddress> clientList;
        DatagramSocket socketDirectory;
        DatagramPacket packet;
        DatagramSocket socket = null;
        ObjectInputStream in;
        Object obj;
        
        if(args.length != 1){
            System.out.println("Sintaxe: java DirectoryService portoDeEscuta");
            return;
        }
        
        serverList = new ArrayList<>();
       
        try {
            socketDirectory = new DatagramSocket(Integer.parseInt(args[0]));
            packet = new DatagramPacket(new byte[256], 256);
            while(true){
          
               // socket = socketDirectory.accept();
                socketDirectory.receive(packet);
                
                in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
            
                obj = in.readObject();
                
                if( obj instanceof ConfirmationMessage){
                    ConfirmationThread ct = new ConfirmationThread(serverList, (ConfirmationMessage) obj, socket);
                    ct.start();
                }
                    
            }
        } catch (IOException ex) {
            Logger.getLogger(DirectoryService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DirectoryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}