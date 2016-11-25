package directoryservice;

import DataMessaging.ConfirmationMessage;
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



public class ServerThread extends Thread {
    public static final int TIMEOUT = 30000; // 30 segundos timeout
    List<DataAddress> list;
    DatagramPacket packet;
    DatagramSocket socket;
    ConfirmationMessage cm;
    ObjectOutputStream out;
    ObjectInputStream in;
    DataAddress dataAddress;
    ByteArrayOutputStream bOut;
    ServerMessage sm;
    Map<String,List<DataAddress>> mapServers;
            
    public ServerThread(List<DataAddress> list, Map<String,List<DataAddress>> mapServers, ConfirmationMessage cm, DatagramSocket socket, DatagramPacket packet) {
        this.list = list;
        this.mapServers = mapServers;
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
        try {
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
            do{
                socket.setSoTimeout(TIMEOUT);
                socket.receive(packet);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                
                //Ler objecto serializado
                Object objecto = in.readObject();
                
                sm = (ServerMessage) objecto;
                
                if(sm.isChanges())
                    mapServers.put(sm.getName(), sm.getUsers());
            }while(true);
        }catch (SocketTimeoutException e){
            mapServers.remove(sm.getName());
            for(DataAddress i: list)
                if(i.getName().equals(sm.getName())){
                    list.remove(i);
                    break;
                }
        }catch (SocketException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(socket != null)
                socket.close();
        }
    }
}