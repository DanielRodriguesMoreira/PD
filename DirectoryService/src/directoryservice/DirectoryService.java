package directoryservice;

import Constants.Constants;
import DataMessaging.ClientMessage;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    // <editor-fold defaultstate="collapsed" desc=" Variáveis ">
    static Map<DataAddress,List<DataAddress>> mapServers;    // Mapa de Lista de Clientes com chave de Servidor 
    static List<DataAddress> listClients;                    // Lista de Clients Ativos
    static DatagramPacket packet;
    static DatagramSocket socket = null;
    static Object obj;
    static ByteArrayOutputStream bOut;
    static ObjectOutputStream out;
    // </editor-fold>
    
    public static void main(String[] args)
    {
        // (Saber os clientes que estão ligados a um determinado servidor)
        mapServers = new TreeMap();
        listClients = new ArrayList<>();
        
        try {
            // <editor-fold defaultstate="collapsed" desc=" Args ">
            if(args.length != 1){
                System.out.println("Sintaxe: DirectoryService port");
                return;
            }
            
            int port = Integer.parseInt(args[0]);
                
            //Criar novo datagramSocket
            socket = new DatagramSocket(port);
            // </editor-fold>
            while(true){
                // <editor-fold defaultstate="collapsed" desc=" Recebe Mensagem ">
                //Receber resposta
                packet = new DatagramPacket(new byte[DATAGRAM_MAX_SIZE], DATAGRAM_MAX_SIZE);
                socket.receive(packet);
                System.out.println("<DIR> Recebi um pacote");
            
                //Criar object inputStream
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                
                //Ler objecto serializado
                Object objecto = in.readObject();
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" ServerMessage ">
                if( objecto instanceof ServerMessage) {
                    ServerMessage serverMessage = (ServerMessage) objecto;
                    switch(serverMessage.getRequest()){
                        // <editor-fold defaultstate="collapsed" desc=" SERVER_MSG_CHECK_USERNAME ">
                        case SERVER_MSG_CHECK_USERNAME:
                            cleanListServers();
                            if(checkExistsServer(serverMessage.getServer()))
                                serverMessage.setExists(true);
                            sendMessage(serverMessage);
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc=" SERVER_MSG_HEARTBEAT ">
                        case SERVER_MSG_HEARTBEAT:
                            mapServers.put(serverMessage.getServer(), serverMessage.getUsers());
                            cleanListServers();
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc=" SERVER_MSG_UPDATE_LIST ">
                        case SERVER_MSG_UPDATE_LIST:
                            mapServers.put(serverMessage.getServer(), serverMessage.getUsers());
                            cleanListServers();
                            break;
                        // </editor-fold>
                    }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" ClientMessage ">
                } else if(objecto instanceof ClientMessage) {
                    ClientMessage clientMessage = (ClientMessage) objecto;
                    switch(clientMessage.getRequest()){
                        case SERVER_MSG_CHECK_USERNAME:
                            
                    }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Erros, Exceptions & Closes ">
                } else {
                    System.out.println("Não sei que tipo e' a mensagem.");
                }
            }
        } catch (SocketException ex) {
            System.out.println("SocketException >> " + ex);
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("IOException >> " + ex);
        }
        if(socket != null)
            socket.close();
        // </editor-fold>
    }
    
    private static long getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();
    }
    
    private static void cleanListServers(){
        long currentTime = getCurrentTime();
        List<DataAddress> listServers = new ArrayList<>( mapServers.keySet());
        for(DataAddress i : listServers)
            if((currentTime - i.getTime()) > HEARTBEAT)
                mapServers.remove(i);
    }
    
    private static <T> void sendMessage(T message){
        try {
            bOut = new ByteArrayOutputStream(1000);
            out = new ObjectOutputStream(bOut);
            out.writeObject(message);

            packet.setData(bOut.toByteArray());
            packet.setLength(bOut.size());

            socket.send(packet);
         } catch (IOException ex) {
            System.out.println("IOException >> " + ex);
        }
    }

    private static boolean checkExistsServer(DataAddress addr){
        List<DataAddress> listServers = new ArrayList<>( mapServers.keySet());
        for(DataAddress i : listServers)
            if(addr.equals(i))
                return true;
        return false;
    }
}