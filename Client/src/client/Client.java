
package client;

import Constants.Constants;
import DataMessaging.ClientMessage;
import DataMessaging.DataAddress;
import Threads.ImAliveThread;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class Client implements Constants {
    String username;
    String directoryServiceIP;
    String directoryServicePort;
    DatagramSocket dataSocket = null;
    DataAddress dataAddress = null;

    ByteArrayOutputStream bOut = null;
    ObjectOutputStream out = null;
    ObjectInputStream in;
    DatagramPacket packet;
    ClientMessage message;

    InetAddress serverDirectoryAddr;
    int serverDirectoryPort;
    List<DataAddress> OnlineServers;
    List<DataAddress> OnlineClients;
        
    public Client (String username, String directoryServiceIP, String directoryServicePort) {
        this.username = username;
        this.directoryServiceIP = directoryServiceIP;
        this.directoryServicePort = directoryServicePort;
    }    

    public void sendMessageToServiceDirectory(ClientMessage messageToDirectory,String tipoPedidoAExecutar) {
        try {
            serverDirectoryAddr = InetAddress.getByName(this.directoryServiceIP);
            serverDirectoryPort = Integer.parseInt(this.directoryServicePort);   
            dataSocket = new DatagramSocket();

            bOut = new ByteArrayOutputStream();            
            out = new ObjectOutputStream(bOut);

            /*  Criar o datagramAddress para enviar para o Service directory    */
            dataAddress = new DataAddress(username, null, -1, -1);
            message = new ClientMessage(dataAddress, null, null, tipoPedidoAExecutar, null, null, false);
            
            out.writeObject(message);
            out.flush();

            packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), serverDirectoryAddr, serverDirectoryPort);
            dataSocket.send(packet);
            System.out.println("<Client> Enviei mensagem\n");

        } catch (SocketException ex) {
            System.out.println("Erro ao criar o DatagramSocket\n");
        } catch (IOException ex) {
            System.out.println("Erro ao criar objecto serializado\n");
        }
    }

    public String OnlineServerstoString() {
        String teste = null;
        if(OnlineServers != null)
        {
            for(int i = 0; i < OnlineServers.size(); i++) {
                teste += this.OnlineServers.get(i).getName();
            }
        }
        if(teste == null)
            teste = "No Server Active\n";
        return teste;
    }
    
    public void receiveMessage() {
        try {
            /*  Receber a resposta da directory service  */
            packet = new DatagramPacket(new byte[DATAGRAM_MAX_SIZE], DATAGRAM_MAX_SIZE);
            dataSocket.receive(packet);

            in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));

            /*  Ler a lista de servidores ligados   */
            System.out.println("<Client> Packet received");
            message = (ClientMessage) in.readObject();
            

            //mandar para a thread o porto em condicoes
            //serverDirectoryPort = packet.getPort();

            /*if (!message.isExists()) {
            Thread t1 = new ImAliveThread(dataSocket, serverDirectoryAddr, serverDirectoryPort, dataAddress);
            t1.start();
            
            this.OnlineServers = message.getListServers();
            }*/
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean checkClientExists() {
        sendMessageToServiceDirectory(message, CLIENT_MSG_CHECK_USERNAME);
        receiveMessage();
        return message.isExists();
    }
}
