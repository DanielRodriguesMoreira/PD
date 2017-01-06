


/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public interface Constants {
    public static final int DATAGRAM_MAX_SIZE = 10000;
    public static final int HEARTBEAT = 30 * 1000; //30 segundos
    public static final String CLIENT_GET_ONLINE_SERVERS = "ClientGetOnlineServers";
    public static final String CLIENT_GET_ONLINE_CLIENTS = "ClientGetOnlineClients";
    public static final String CLIENT_GET_ALL_LISTS = "ClientGetAllLists";
    public static final String CLIENT_MSG_CHECK_USERNAME = "ClientCheckUsername";
    public static final String CLIENT_MSG_HEARTBEAT = "ClientHeartbeat";
    public static final String CLIENT_SENDMESSAGE = "SendMessage";
    public static final String CLIENT_SENDMESSAGE_TOALL = "SendMessageToAll";
}
