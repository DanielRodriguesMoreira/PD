
package Threads;

import java.net.Socket;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class AttendTCPClientsThread extends Thread{

    private Socket toClientSocket;
    
    public AttendTCPClientsThread(Socket socket){
        this.toClientSocket = socket;
    }
    
    @Override
    public void run(){
        
        while(true){
            
        }
        
    }
}
