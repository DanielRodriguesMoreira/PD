
package server;

import Constants.Constants;
import Threads.ImAliveThread;
import DataMessaging.DataAddress;
import DataMessaging.ServerMessage;
import Exceptions.ServerAlreadyExistsException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class Server implements Constants{

    public static final int TIMEOUT = 10; //segundos
    
    public static void main(String[] args) {
        
        String serverName = null;
        InetAddress directoryServiceAddress;
        int directoryServicePort;
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        ByteArrayOutputStream bOut = null;
        ObjectOutputStream out = null;
        ObjectInputStream in;
        ServerSocket serverSocket = null;
        int socketTCPPort;
        
        if(args.length != 3){
            System.out.println("Sintaxe: java Server <name> <DirectoryServiceAddress> <DirectoryServicePort>");
            return;
        }
        
        
        try {
            
            // <editor-fold defaultstate="collapsed" desc=" Fill serverName and directoryService Adress ">
            serverName = args[0];
            directoryServiceAddress = InetAddress.getByName(args[1]);
            directoryServicePort = Integer.parseInt(args[2]);
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" Create UDP socket ">
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT*1000);
            // </editor-fold>
            //
            bOut = new ByteArrayOutputStream();            
            out = new ObjectOutputStream(bOut);

//DANIEL: Com que valores tens que preencher o dataAddress da serverMessage???
            ServerMessage serverMessage = new ServerMessage(null, null, false, false);
            
            out.writeObject(serverMessage);
            out.flush();
            
            //
            packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), directoryServiceAddress, directoryServicePort);
            socket.send(packet);
            
            
            packet = new DatagramPacket(new byte[DATAGRAM_MAX_SIZE], DATAGRAM_MAX_SIZE);
            socket.receive(packet);
            
            in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));                
            serverMessage = (ServerMessage)in.readObject();
            
            checkIfServerAlreadyExists(serverMessage);
            
            //Criar socket TCP
            serverSocket = new ServerSocket();
            socketTCPPort = serverSocket.getLocalPort();
            //Criar thread que vai estar a receber pedidos via TCP
            
//DANIEL -> Tens que alterar isto porque o que queres mandar é o nome, o IP e o porto de escuta automático TCP
            // <editor-fold defaultstate="collapsed" desc=" Create DataAddress object with serverName, serverAddress and serverPort ">
            DataAddress myAddress = new DataAddress(serverName, InetAddress.getLocalHost(), socket.getLocalPort());
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc=" Create and start ImAliveThread ">
            Thread t1 = new ImAliveThread(socket, directoryServiceAddress, 
                    directoryServicePort, myAddress);
            t1.start();
            // </editor-fold>
            
        } catch(ServerAlreadyExistsException ex) {
            System.out.println(ex.getError());
        } catch(UnknownHostException ex) {
            System.out.println("Can't find directory service " + serverName);
        } catch(NumberFormatException e){
            System.out.println("The server port must be a positive integer.");
        } catch(SocketTimeoutException e){
            System.out.println("Não foi recebida qualquer resposta:\n\t"+e);
        } catch(SocketException ex) {
            System.out.println("An error occurred with the UDP socket level:\n\t" + ex);
        } catch(IOException ex) {
            System.out.println("An error occurred in accessing the socket:\n\t" + ex);
        } catch(ClassNotFoundException ex) {
            System.out.println("The object received is not the expected type:\n\t" + ex);
        }finally{
            if(socket != null)
                socket.close();
        } 
    }
    
    public static void checkIfServerAlreadyExists(ServerMessage serverMessage) throws ServerAlreadyExistsException{
        if(serverMessage.getExists()) 
            throw new ServerAlreadyExistsException(serverMessage.getServerName());
        else{
            System.out.println("Servidor não existe!");
            System.out.println("Pumba! Toma la que isto ja bomba!");
        }
    }

}
