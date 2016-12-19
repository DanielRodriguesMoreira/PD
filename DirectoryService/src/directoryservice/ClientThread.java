
package directoryservice;

import Constants.Constants;
import DataMessaging.ClientMessage;
import DataMessaging.DataAddress;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ClientThread extends Thread implements Constants {
    List<DataAddress> listServers;
    List<String> listClients;
    DatagramPacket packet;
    DatagramSocket socket;
    ByteArrayOutputStream bOut;
    ObjectOutputStream out;
    ClientMessage message;           // Messangem do Cliente
    
    public ClientThread(List<DataAddress> listServers, List<String> listClients, ClientMessage message, DatagramSocket socket, DatagramPacket packet) {
        this.listServers = listServers;
        this.listClients = listClients;
        this.packet = packet;
        this.socket = socket;
        this.message = message;
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
    public void run() 
    {
        
        System.out.println("<ClientThread> Recebi mensagem do " + message.getUser());

        if(message.getRequest().equalsIgnoreCase(GETONLINESERVERS)) {
            System.out.println("<ClientThread> Vou mandar a lista de servidores");
            message.setListServers(listServers);
            sendMessage(message);
        } else if (message.getRequest().equalsIgnoreCase(GETONLINECLIENTS)) {
            System.out.println("<ClientThread> Vou mandar a lista de clientes");
        }
    }
}
