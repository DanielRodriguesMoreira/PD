package directoryservice;

import DataMessaging.ConfirmationMessage;
import DataMessaging.DataAddress;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
<<<<<<< HEAD
=======
import java.net.Socket;
>>>>>>> origin/master
import java.util.List;

public class ConfirmationThread extends Thread {
    List<DataAddress> list;
<<<<<<< HEAD
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
=======
    DatagramSocket socket;
    ConfirmationMessage cm;
    ObjectOutputStream out;
    DatagramPacket packet;
            
    public ConfirmationThread(List<DataAddress> list, ConfirmationMessage cm, DatagramSocket socket ){
        this.list = list;
        this.cm = cm;
        this.socket = socket;
        packet = new DatagramPacket(new byte[256], 256);
    }
    public void run(){
        for(DataAddress i : list)
            if(i.getName() == cm.getServerName()){
                cm.setExists(true);

                try {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream(256);
                    out = new ObjectOutputStream(bout);
                    
                    out.writeUnshared(cm);
                    packet.setData(bout.toByteArray());
                    packet.setLength(bout.size());
                    socket.send(packet);
                } catch (IOException ex) {
                    Logger.getLogger(ConfirmationThread.class.getName()).log(Level.SEVERE, null, ex);
>>>>>>> origin/master
                }
            }
<<<<<<< HEAD
            cm.setExists(true);
            bOut = new ByteArrayOutputStream(1000);
            out = new ObjectOutputStream(bOut);
            out.writeObject(cm);

            packet.setData(bOut.toByteArray());
            packet.setLength(bOut.size());
            
            socket.send(packet);
            
=======
        cm.setExists(false);
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(256);
            out = new ObjectOutputStream(bout);

            out.writeUnshared(cm);
            packet.setData(bout.toByteArray());
            packet.setLength(bout.size());
            socket.send(packet);
>>>>>>> origin/master
        } catch (IOException ex) {
            System.out.println("<DirectoryService> " + ex);
        }
    }
}