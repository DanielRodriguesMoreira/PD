package directoryservice;

import Threads.CommandThread;
import Constants.Constants;
import static Constants.Constants.HEARTBEAT;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

/**
 * Está sempre à escuta, dependendo do tipo de mensagem faz uma tarefa diferente.
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
        mapServers = new TreeMap<>();
        listClients = new ArrayList<>();
        List<DataAddress> listServers;
        DataAddress addr;
        
        CommandThread ct = new CommandThread(mapServers,listClients);
        ct.start();
        
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
                System.out.println("<DIR> packet received");
            
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
                            else{
                                serverMessage.getServer().setTime(getCurrentTime());
                                mapServers.put(serverMessage.getServer(), serverMessage.getUsers());
                            }
                            sendMessage(serverMessage);
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc=" SERVER_MSG_HEARTBEAT ">
                        case SERVER_MSG_HEARTBEAT:
                            if(checkExistsServer(serverMessage.getServer())){
                                List<DataAddress> listAux = mapServers.get(serverMessage.getServer());
                                mapServers.remove(serverMessage.getServer());
                                serverMessage.getServer().setTime(getCurrentTime());
                                mapServers.put(serverMessage.getServer(), listAux);
                            }
                            cleanListServers();
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc=" SERVER_MSG_UPDATE_LIST ">
                        case SERVER_MSG_UPDATE_LIST:
                            if(checkExistsServer(serverMessage.getServer())){
                                mapServers.remove(serverMessage.getServer());
                                serverMessage.getServer().setTime(getCurrentTime());
                                mapServers.put(serverMessage.getServer(), serverMessage.getUsers());
                            }
                            cleanListServers();
                            break;
                        // </editor-fold>
                    }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" ClientMessage ">
                } else if(objecto instanceof ClientMessage) {
                    ClientMessage clientMessage = (ClientMessage) objecto;
                    ClientMessage messageToSend;
                    switch(clientMessage.getRequest()){
                        // <editor-fold defaultstate="collapsed" desc=" CLIENT_MSG_CHECK_USERNAME ">
                        case CLIENT_MSG_CHECK_USERNAME:
                            cleanListClients();
                            boolean exists = false;
                            if(checkExistsClient(clientMessage.getDataAddress()))
                                exists = true;
                            else{
                                clientMessage.getDataAddress().setTime(getCurrentTime());
                                listClients.add(clientMessage.getDataAddress());
                            }
                            
                            messageToSend = new ClientMessage(clientMessage.getDataAddress(), null, 
                                    null, CLIENT_MSG_CHECK_USERNAME, null, null, exists);
                            sendMessage(messageToSend);
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc=" CLIENT_MSG_HEARTBEAT ">
                        case CLIENT_MSG_HEARTBEAT:
                            clientMessage.getDataAddress().setTime(getCurrentTime());
                            updateTimeClient(clientMessage.getDataAddress());
                            cleanListClients();
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc=" CLIENT_GET_ONLINE_SERVERS ">
                        case CLIENT_GET_ONLINE_SERVERS:
                            listServers = new ArrayList<>( mapServers.keySet());
                            messageToSend = new ClientMessage(clientMessage.getDataAddress(), null, 
                                    null, CLIENT_GET_ONLINE_SERVERS, null, null, false);
                            messageToSend.setListServers(listServers);
                            sendMessage(messageToSend);
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc=" CLIENT_GET_ONLINE_CLIENTS ">
                        case CLIENT_GET_ONLINE_CLIENTS:
                            messageToSend = new ClientMessage(clientMessage.getDataAddress(), null, 
                                    null, CLIENT_GET_ONLINE_CLIENTS, null, null, false);
                            messageToSend.setListClients(listClients);
                            sendMessage(messageToSend);
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc=" CLIENT_GET_ALL_LISTS ">
                        case CLIENT_GET_ALL_LISTS:
                            cleanListServers();
                            cleanListClients();                          
                            listServers = new ArrayList<>( mapServers.keySet());
                            messageToSend = new ClientMessage(clientMessage.getDataAddress(), null, 
                                    null, CLIENT_GET_ALL_LISTS, null, null, false);
                            messageToSend.setListServers(listServers);
                            messageToSend.setListClients(listClients);
                            sendMessage(messageToSend);
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc=" CLIENT_SENDMESSAGE ">
                        case CLIENT_SENDMESSAGE:
                            cleanListServers();
                            if(checkLoggedClient(clientMessage.getDataAddress()) && checkLoggedClient(clientMessage.getUsernameToSend())){
                                packet.setAddress(clientMessage.getUsernameToSend().getIp());
                                packet.setPort(clientMessage.getUsernameToSend().getPort());
                                sendMessage(clientMessage);
                            }
                            break;
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc=" CLIENT_SENDMESSAGE_TOALL ">
                        case CLIENT_SENDMESSAGE_TOALL:
                            cleanListServers();
                            if(checkLoggedClient(clientMessage.getDataAddress()))
                                sendMessageToAllLogged(clientMessage);
                            break;
                        // </editor-fold>
                    }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Erros, Exceptions & Closes ">
                } else {
                    throw new ClassNotFoundException();
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
        Iterator iterator = listServers.iterator();
        while (iterator.hasNext()) {
            DataAddress item = (DataAddress) iterator.next();
            if((currentTime - item.getTime()) > HEARTBEAT + 1000)
                mapServers.remove(item);
        }
    }
    
    private static void cleanListClients(){
        long currentTime = getCurrentTime();
        if(listClients != null){
            Iterator iterator = listClients.iterator();
            while (iterator.hasNext()) {
                DataAddress item = (DataAddress) iterator.next();
                if((currentTime - item.getTime()) > HEARTBEAT + 1000) {
                   iterator.remove();
                }
            }
        }
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
    
    private static boolean checkExistsClient(DataAddress addr){
        if(listClients != null)
            for(DataAddress i : listClients)
                if(addr.equals(i))
                    return true;
        return false;
    }
    
    private static void updateTimeClient(DataAddress addr){
        for(int i = 0; i < listClients.size(); i++)
            if(listClients.get(i).equals(addr)){
                listClients.set(i, addr);
                break;
            }
    }
    
    private static boolean checkLoggedClient(DataAddress addr){
        for(DataAddress i : mapServers.keySet()){
            if(mapServers.get(i) != null)
                if(mapServers.get(i).contains(addr))
//                for(DataAddress j : mapServers.get(i))
//                    if(addr.equals(j))
                    return true;
        }
        return false;
    }
    
    private static void sendMessageToAllLogged(ClientMessage clientMessage){
        List<DataAddress> listLogged = new ArrayList<>();
        List<DataAddress> listServers = new ArrayList<>( mapServers.keySet());
        for(DataAddress i : listServers)
            for(DataAddress j : mapServers.get(i))
                if(!listLogged.contains(j)){
                    packet.setAddress(j.getIp());
                    packet.setPort(j.getPort());
                    sendMessage(clientMessage);
                    listLogged.add(j);
                }
    }
}