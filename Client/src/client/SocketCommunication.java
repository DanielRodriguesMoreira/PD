package client;

import DataMessaging.DataAddress;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */
public class SocketCommunication {
    private DataAddress dataAddr = null;
    private Socket socketTCP = null;
    private ObjectOutputStream outTCP = null;
    private ObjectInputStream inTCP = null;

    public SocketCommunication(DataAddress dataAddr) throws IOException {
        this.dataAddr = dataAddr;
        this.socketTCP = new Socket(dataAddr.getIp(), dataAddr.getPort());
        this.inTCP = new ObjectInputStream(this.socketTCP.getInputStream());
        this.outTCP = new ObjectOutputStream(this.socketTCP.getOutputStream());
    }
}
