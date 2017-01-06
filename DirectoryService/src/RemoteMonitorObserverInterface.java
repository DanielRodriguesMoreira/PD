

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public interface RemoteMonitorObserverInterface extends Remote{
    public void updateInformation(String information) throws RemoteException;
}
