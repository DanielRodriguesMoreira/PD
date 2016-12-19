
package directoryservice;

import Constants.Constants;
import static Constants.Constants.HEARTBEAT;
import DataMessaging.ClientMessage;
import DataMessaging.DataAddress;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
        
        try {
            System.out.println("entrei na thread client");
            for(String i : listClients){
                if(i.equalsIgnoreCase(message.getUser())){
                    message.setExists(true);
                    sendMessage(message); // Cliente já existe na lista.
                    System.out.println("Cliente ja existe.");
                    return;
                }
            }
            listClients.add(message.getUser());
            sendMessage(message); // Confirmar ao Servidor que entrou na lista.
            do{
                socket.setSoTimeout(HEARTBEAT);
                packet = new DatagramPacket(new byte[DATAGRAM_MAX_SIZE], DATAGRAM_MAX_SIZE);
                socket.receive(packet);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                System.out.println("Recebi o segundo pacote ");
                //Ler objecto serializado
                
                message = (ClientMessage) in.readObject();
                
                 if(message.getRequest().equalsIgnoreCase(GETONLINESERVERS)) {
                    System.out.println("<ClientThread> Vou mandar a lista de servidores");
                    DataAddress servidor = new DataAddress("Daniel", null, 1);
                    DataAddress servidor2 = new DataAddress("Hugo", null, 1);
                    DataAddress servidor3 = new DataAddress("Tiago", null, 1);
                    listServers.add(servidor);
                    listServers.add(servidor2);
                    listServers.add(servidor3);
                    message.setListServers(listServers);
                    sendMessage(message);
                } else if (message.getRequest().equalsIgnoreCase(GETONLINECLIENTS)) {
                    System.out.println("<ClientThread> Vou mandar a lista de clientes");
                }
                
                System.out.println("Recebi mensagem");
            }while(true);
        }catch (SocketTimeoutException e){
            for(String i: listClients)
                if(i.equals(message.getUser())){
                    listClients.remove(i);
                    break;
                }
        }catch (SocketException ex) {
            System.out.println("Socket "+ ex);
        }catch (IOException ex) {
            System.out.println("IOException "+ ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFound "+ ex);
        }finally{
            if(socket != null)
                socket.close();
        }
    }
}
