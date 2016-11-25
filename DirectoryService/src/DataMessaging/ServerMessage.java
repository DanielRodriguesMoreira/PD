package DataMessaging;

import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ServerMessage {
    String name;
    int port;
    List<DataAddress> users;
    boolean changes;

    public ServerMessage(String name, int port, List<DataAddress> users, boolean changes) {
        this.name = name;
        this.port = port;
        this.users = users;
        this.changes = changes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<DataAddress> getUsers() {
        return users;
    }

    public void setUsers(List<DataAddress> users) {
        this.users = users;
    }

    public boolean isChanges() {
        return changes;
    }

    public void setChanges(boolean changes) {
        this.changes = changes;
    }
}
