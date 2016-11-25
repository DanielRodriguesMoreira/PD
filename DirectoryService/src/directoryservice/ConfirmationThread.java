package directoryservice;

import DataMessaging.ConfirmationMessage;
import DataMessaging.DataAddress;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ConfirmationThread extends Thread {
    List<DataAddress> list;
    DatagramPacket packet;
    DatagramSocket socket;
    ConfirmationMessage cm;
    ObjectOutputStream out;
    ObjectInputStream in;
    DataAddress dataAddress;
    ByteArrayOutputStream bOut;
            
    public ConfirmationThread(List<DataAddress> list, ConfirmationMessage cm, DatagramSocket socket, DatagramPacket packet) {
        this.list = list;
        this.cm = cm;
        this.socket = socket;
        this.packet = packet;
    }
    
    @Override
    public void run() {
        try {
            for(DataAddress i : list){
                if(i.getName().equalsIgnoreCase(cm.getServerName()))
                {
                    cm.setExists(true);
                }
            }
            cm.setExists(true);
            bOut = new ByteArrayOutputStream(1000);
            out = new ObjectOutputStream(bOut);
            out.writeObject(cm);

            packet.setData(bOut.toByteArray());
            packet.setLength(bOut.size());
            
            socket.send(packet);
        } catch (IOException ex) {
            System.out.println("<DirectoryService> " + ex);
        }
    }
}