
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public interface RemoteGetServersInterface extends Remote{
    public void addClientObserver(RemoteClientObserverInterface clientRef, DataAddress myAddress) throws RemoteException;
    public void removeClientObserver(RemoteClientObserverInterface clientRef) throws RemoteException;
}
