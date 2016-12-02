package directoryservice;

import DataMessaging.DataAddress;
import DataMessaging.ServerMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class DirectoryService {
    DatagramPacket packet;
    DatagramSocket socket = null;
    Object obj;
    int cont = 0;
    Map<String,List<DataAddress>> mapServers;
    List<DataAddress> serverList;
            
    public void main(String[] args) 
    {
        //Lista de servidores conectados
        serverList = new ArrayList<>();
        //List<DataAddress> clientList;
        //Mapa de Lista de Clientes com chave de Servidor
        mapServers = new TreeMap();
        try {
            
            if(args.length != 1){
                System.out.println("Sintaxe: DirectoryService port");
                return;
            }
            
            int port = Integer.parseInt(args[0]);
                
            //Criar novo datagramSocket
            socket = new DatagramSocket(port);
        
            while(true) 
            {
                cont++;
                //Receber resposta
                packet = new DatagramPacket(new byte[1000], 1000);
                socket.receive(packet);
                System.out.println("Recebi pacote");
            
                //Criar object inputStream
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                
                //Ler objecto serializado
                Object objecto = in.readObject();
                DatagramSocket s = new DatagramSocket(port+cont);
                
                if( objecto instanceof ServerMessage) {
                    ServerMessage cm = (ServerMessage) objecto;
                    ServerThread ct = new ServerThread(serverList, mapServers, cm, socket, packet);
                } else if(objecto instanceof String) {
                    //É porque é um cliente
                    String clientMessage = (String) objecto; 
                    ClientThread ct = new ClientThread(serverList, clientMessage, socket, packet);
                    ct.start();
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(DirectoryService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(DirectoryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}