
package Threads;

import DataMessaging.DataAddress;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ServerTCPConnection extends Thread{
    private Socket socketTCP = null;
    private ObjectOutputStream outTCP = null;
    private ObjectInputStream inTCP = null;
    private DataAddress serverAddress = null;
    
    public ServerTCPConnection(DataAddress serverToConnect){
        try {
            this.serverAddress = serverToConnect;
            this.prepareSocketTCP();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    @Override
    public void run(){
        
    }
    
    private void prepareSocketTCP() throws IOException{
        
        this.socketTCP = new Socket(this.serverAddress.getIp(), this.serverAddress.getPort());
        
        this.inTCP = new ObjectInputStream(this.socketTCP.getInputStream());
        this.outTCP = new ObjectOutputStream(this.socketTCP.getOutputStream());
        
    }

}
