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

public class ServerThread extends Thread {
    List<DataAddress> list;
    DatagramPacket packet;
    DatagramSocket socket;
    ConfirmationMessage cm;
    ObjectOutputStream out;
    ObjectInputStream in;
    DataAddress dataAddress;
    ByteArrayOutputStream bOut;
            
    public ServerThread(List<DataAddress> list, ConfirmationMessage cm, DatagramSocket socket, DatagramPacket packet) {
        this.list = list;
        this.cm = cm;
        this.socket = socket;
        this.packet = packet;
    }
    
    private <T> void sendMessage(T message){
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
            for(DataAddress i : list){
                if(i.getName().equalsIgnoreCase(cm.getServerName())){
                    cm.setExists(true);
                    sendMessage(cm); // Servidor já existe na lista.
                    return;
                }
            }
            dataAddress = new DataAddress(cm.getServerName(), packet.getAddress(), packet.getPort());
            list.add(dataAddress);
            sendMessage(cm); // Confirmar ao Servidor que entrou na lista.
    }
}