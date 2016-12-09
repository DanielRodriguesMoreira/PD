
package client;

import Constants.Constants;
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

    public static void main(String[] args) {
        DatagramSocket dataSocket = null;
        ByteArrayOutputStream bOut = null;
        ObjectOutputStream out = null;
        DatagramPacket packet;
        ObjectInputStream in;
        
        List<String> OnlineServers;
        InetAddress serverDirectoryAddr;
        int serverDirectoryPort;
        
        if(args.length != 2){
            System.out.println("Sintax: java Client serverAddress serverUdpPort");
            return;
        }
        
        try {
            serverDirectoryAddr = InetAddress.getByName(args[0]);
            serverDirectoryPort = Integer.parseInt(args[1]);   
            dataSocket = new DatagramSocket();
            
            bOut = new ByteArrayOutputStream();            
            out = new ObjectOutputStream(bOut);

            out.writeObject(GETONLINESERVERS);
            out.flush();
            
            packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), serverDirectoryAddr, serverDirectoryPort);
            dataSocket.send(packet);
            
            /*  Receber a resposta da directory service  */
            packet = new DatagramPacket(new byte[DATAGRAM_MAX_SIZE], DATAGRAM_MAX_SIZE);
            dataSocket.receive(packet);
            
            in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
            
            /*  Ler a lista de servidores ligados   */
            //OnlineServers = (List<String>) in.readObject();
            System.out.println("<Client> Packet received");
            System.out.println("<Client> Recebi-> "+ in.readObject());
            
        } catch (SocketException ex) {
            System.out.println("Erro ao criar o DatagramSocket\n");
        } catch (IOException ex) {
            //Erro a criar objecto serializado
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            //Erro ao ler o objecto serializado
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            if(dataSocket != null){
                dataSocket.close();
            }
        }
        
    }

}
