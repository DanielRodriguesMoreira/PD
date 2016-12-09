
package directoryservice;

import Constants.Constants;
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

public class ClientThread extends Thread implements Constants {
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

        if(mensagemDoCliente.equalsIgnoreCase(GETONLINESERVERS)) {
            System.out.println("<ClientThread> Vou mandar a lista de servidores");
        } else if (mensagemDoCliente.equalsIgnoreCase(GETONLINECLIENTS)) {
            System.out.println("<ClientThread> Vou mandar a lista de clientes");
        }
    }
}
