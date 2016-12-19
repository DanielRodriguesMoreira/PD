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

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

/**
 *  Cada Thread vai tratar de um servidor especifico, logo tem que ter um porto especifico
 */
public class ServerThread extends Thread implements Constants {
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
        // <editor-fold defaultstate="collapsed" desc=" Iniciar variáveis.">
        this.list = list;
        this.mapServers = mapServers;
        this.sm = sm;
        this.packet = packet;
        this.port = packet.getPort();
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException ex) {
            System.out.println("SocketException >> " + ex);
        }
        // </editor-fold>
    }
    
    private <T> void sendMessage(T message){
        // <editor-fold defaultstate="collapsed" desc=" Enviar Mensagem.">
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
        // </editor-fold>
    }
    
    @Override
    public void run() {
        try {
            // <editor-fold defaultstate="collapsed" desc=" Verifica se o Servido existe, senão existir adiciona.">
            for(DataAddress i : list){
                if(i.getName().equalsIgnoreCase(sm.getServer().getName())){
                    sm.setExists(true);
                    sendMessage(sm); // Servidor já existe na lista.
                    return;
                }
            }
            dataAddress = new DataAddress(sm.getServer().getName(), packet.getAddress(), packet.getPort(), -1);
            list.add(dataAddress);
            sendMessage(sm); // Confirmar ao Servidor que entrou na lista.
            // </editor-fold>
            do{
                socket.setSoTimeout(HEARTBEAT);
                packet = new DatagramPacket(new byte[DATAGRAM_MAX_SIZE], DATAGRAM_MAX_SIZE);
                socket.receive(packet);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                
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