
package Threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class AcceptTCPClientsThread extends Thread{

    private ServerSocket serverSocket = null;
    private Socket toClientSocket = null;
    
    public AcceptTCPClientsThread(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    
    @Override
    public void run(){
        
        while(true){     
            try {
                //Accept Client
                toClientSocket = serverSocket.accept();
                
                //Start thread to attend the client
                Thread attendClientThread = new AttendTCPClientsThread(this.toClientSocket);
                attendClientThread.start();
                
            } catch (IOException ex) {
                System.out.println("An error occurred in accessing the socket:\n\t" + ex);
            } 
        }
    }
}
