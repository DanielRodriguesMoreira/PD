package directoryservice;

import DataMessaging.ConfirmationMessage;
import DataMessaging.DataAddress;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfirmationThread extends Thread{
    List<DataAddress> list;
    Socket socket;
    ConfirmationMessage cm;
    ObjectOutputStream out;
            
    public ConfirmationThread(List<DataAddress> list, ConfirmationMessage cm, Socket socket ){
        this.list = list;
        this.cm = cm;
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ConfirmationThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void run(){
        for(DataAddress i : list)
            if(i.getName() == cm.getServerName()){
                cm.setExists(true);
                try {
                    out.writeUnshared(cm);
                } catch (IOException ex) {
                    Logger.getLogger(ConfirmationThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.interrupt();
            }
        cm.setExists(false);
        try {
            out.writeUnshared(cm);
        } catch (IOException ex) {
            Logger.getLogger(ConfirmationThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.interrupt();
    }
}