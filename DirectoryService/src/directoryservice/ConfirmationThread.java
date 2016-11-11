package directoryservice;

import DataMessaging.ConfirmationMessage;
import DataMessaging.DataAddress;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfirmationThread extends Thread{
    List<DataAddress> list;
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
                }
                this.interrupt();
            }
        cm.setExists(false);
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(256);
            out = new ObjectOutputStream(bout);

            out.writeUnshared(cm);
            packet.setData(bout.toByteArray());
            packet.setLength(bout.size());
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(ConfirmationThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.interrupt();
    }
}