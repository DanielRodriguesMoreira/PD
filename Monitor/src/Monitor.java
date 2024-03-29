
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class Monitor extends Observable{

    private String serviceDirectoryIP = null;
    private int serviceDirectoryPort = -1;
    private String serversInformation = null;
    MonitorRemote monitorRemote = null;
    private RemoteGetServersInterface serviceRemote = null;
    
    public Monitor(String sdIP, int sdPort){
    
        this.serviceDirectoryIP = sdIP;
        this.serviceDirectoryPort = sdPort;
        
        try {
            monitorRemote = new MonitorRemote();
            String url = "rmi://" + sdIP + "/RemoteGetServers";
            serviceRemote = (RemoteGetServersInterface)Naming.lookup(url);
            serviceRemote.addMonitorObserver(monitorRemote);
        } catch (RemoteException | NotBoundException | MalformedURLException ex) {
            System.err.println("[Monitor - Remote] " + ex);
        }
        
    }
    
    public String getServersInformation(){
        return this.serversInformation;
    }

    void exit() {
        try {
            serviceRemote.removeMonitorObserver(monitorRemote);
            System.exit(0);
        } catch (RemoteException ex) {
            System.err.println("[Monitor - Remote] " + ex);
        }
    }
    
    class MonitorRemote extends UnicastRemoteObject implements RemoteMonitorObserverInterface{
        
        public MonitorRemote() throws RemoteException {}
        
        @Override
        public void updateInformation(String information) throws RemoteException {
            serversInformation = information;
            setChanged();
            notifyObservers();
        } 
    }

}
