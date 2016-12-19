package directoryservice;

import Constants.Constants;
import DataMessaging.ClientMessage;
import DataMessaging.DataAddress;
import DataMessaging.ServerMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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

/**
 * Está sempre à escuta, dependendo do tipo de mensagem cria uma thread para tratar da tarefa.
 * Criação de Pipes para haver comunicação entre as threads; ServerThread -> UpdateClientsThread; ClientThread -> UpdateClientsThread; ClientThread -> ClientThread;
 */
public class DirectoryService implements Constants {
    
    static Map<String,List<DataAddress>> mapServers;    // Mapa de Lista de Clientes com chave de Servidor 
    static List<DataAddress> listServers;               // Lista de servidores conectados
    static List<String> listClients;                    // Lista de Clients Ativos
    static PipedOutputStream updateClientsPOut;         // Pipe de escrita para o UpdateClientsThread
    static PipedInputStream updateClientsPInp;          // Pipe de leitura do UpdateClientsThread
    
            
    public static void main(String[] args)
    {
        // (Saber os clientes que estão ligados a um determinado servidor)
        mapServers = new TreeMap();
        listServers = new ArrayList<>();
        listClients = new ArrayList<>();
         
       // serverPipe = new PipedOutputStream();
        DatagramPacket packet;
        DatagramSocket socket = null;
        Object obj;
        int cont = 6000;
        
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
                packet = new DatagramPacket(new byte[DATAGRAM_MAX_SIZE], DATAGRAM_MAX_SIZE);
                socket.receive(packet);
                System.out.println("<DIR> Recebi um pacote");
            
                //Criar object inputStream
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                
                //Ler objecto serializado
                Object objecto = in.readObject();
                
                if( objecto instanceof ServerMessage) {
                    ServerMessage sm = (ServerMessage) objecto;
                    ServerThread st = new ServerThread(listServers, mapServers, sm, cont, packet);
                    st.start();
                } else if(objecto instanceof ClientMessage) {
                    ClientMessage clientMessage = (ClientMessage) objecto; 
                    ClientThread ct = new ClientThread(listServers, listClients, clientMessage, socket, packet);
                    ct.start();
                } else {
                    System.out.println("Não sei que tipo e' a mensagem.");
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(DirectoryService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(DirectoryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}