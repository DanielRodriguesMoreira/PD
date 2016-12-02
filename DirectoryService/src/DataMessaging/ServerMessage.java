package DataMessaging;

import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ServerMessage {
    DataAddress server;
    List<DataAddress> users;
    boolean changes;
    private Boolean exists;

    public ServerMessage(DataAddress server, List<DataAddress> users, boolean changes, boolean exists) {
        this.server = server;
        this.users = users;
        this.changes = changes;
        this.exists = exists;
    }

    public DataAddress getServer() {
        return server;
    }

    public void setServer(DataAddress server) {
        this.server = server;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
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