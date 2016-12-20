
package Constants;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */
public interface Constants {
    public static final int DATAGRAM_MAX_SIZE = 10000;
    public static final int HEARTBEAT = 30 * 1000; //30 segundos
    public static final String SERVER_MSG_HEARTBEAT = "ServerHeartbeat";
    public static final String SERVER_MSG_CHECK_USERNAME = "ServerCheckUsername";
    public static final String SERVER_MSG_UPDATE_LIST = "ServerUpdateList";
}
