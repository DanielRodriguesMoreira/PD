package directoryservice;

import Constants.Constants;
import static Constants.Constants.HEARTBEAT;
import DataMessaging.DataAddress;
import DataMessaging.ServerMessage;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

/**
 *  Cada Thread vai tratar de um servidor especifico, logo tem que ter um porto especifico
 */
public class ServerThread extends Thread implements Constants {
    public static final int TIMEOUT = 30000; // 30 segundos timeout
    List<DataAddress> list;
    DatagramPacket packet;
    DatagramSocket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    DataAddress dataAddress;
    ByteArrayOutputStream bOut;
    ServerMessage sm;
    Map<String,List<DataAddress>> mapServers;
    int port;
            
    public ServerThread(List<DataAddress> list, Map<String,List<DataAddress>> mapServers, ServerMessage sm, int port, DatagramPacket packet) {
        this.list = list;
        this.mapServers = mapServers;
        this.sm = sm;
        this.packet = packet;
        this.port = packet.getPort();
        //this.socket = socket;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private <T> void sendMessage(T message){
        try {
            bOut = new ByteArrayOutputStream(DATAGRAM_MAX_SIZE);
            out = new ObjectOutputStream(bOut);
            out.writeObject(message);

            packet.setData(bOut.toByteArray());
            packet.setLength(bOut.size());

            socket.send(packet);
            System.out.println("Enviei para: " + packet.getAddress().getHostAddress() + " : " + packet.getPort());
         } catch (IOException ex) {
            System.out.println("<DirectoryService> " + ex);
        }
    }
    
    @Override
    public void run() {
        try {
            System.out.println("entrei na thread");
            for(DataAddress i : list){
                if(i.getName().equalsIgnoreCase(sm.getServer().getName())){
                    sm.setExists(true);
                    sendMessage(sm); // Servidor já existe na lista.
                    System.out.println("Server ja existe."+port);
                    return;
                }
            }
            dataAddress = new DataAddress(sm.getServer().getName(), packet.getAddress(), packet.getPort());
            list.add(dataAddress);
            sendMessage(sm); // Confirmar ao Servidor que entrou na lista.
            do{
                socket.setSoTimeout(HEARTBEAT);
                socket.receive(packet);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                System.out.println("Recebi o segundo pacote "+port);
                //Ler objecto serializado
                
                sm = (ServerMessage) in.readObject();
                
                if(sm.isChanges()){
                    System.out.println("Houve alteracoes");
                    mapServers.put(sm.getServer().getName(), sm.getUsers());
                }else
                     System.out.println("Acabei");
            }while(true);
        }catch (SocketTimeoutException e){
            mapServers.remove(sm.getServer().getName());
            for(DataAddress i: list)
                if(i.getName().equals(sm.getServer().getName())){
                    list.remove(i);
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