


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public interface RemoteClientObserverInterface extends Remote{
    public void updateServersList(List<String> serversWhereImNotAuthenticated) throws RemoteException;
}
