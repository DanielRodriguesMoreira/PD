
package directoryservice;

import DataMessaging.DataAddress;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */
public class ClientThread extends Thread {
    public static final int MAX_SIZE = 10000;
    public static final String ONLINESERVERS = "OnlineServers";
    public static final String ONLINECLIENTS = "OnlineClients";
    public static final int TIMEOUT = 30000; // 30 segundos timeout
    
    List<DataAddress> list;
    DatagramPacket packet;
    DatagramSocket socket;
    ByteArrayOutputStream bOut;
    ObjectOutputStream out;
    String mensagemDoCliente;
    
    public ClientThread(List<DataAddress> list, String message, DatagramSocket socket, DatagramPacket packet) {
        this.list = list;
        this.packet = packet;
        this.socket = socket;
        this.mensagemDoCliente = message;
    }

    @Override
    public void run() 
    {
        System.out.println("<ClientThread> Recebi ->" + mensagemDoCliente);

        if(mensagemDoCliente.equalsIgnoreCase(ONLINESERVERS)) {
            System.out.println("<ClientThread> Vou mandar a lista de servidores");
        } else if (mensagemDoCliente.equalsIgnoreCase(ONLINECLIENTS)){
            System.out.println("<ClientThread> Vou mandar a lista de clientes");
        }
    }
}
