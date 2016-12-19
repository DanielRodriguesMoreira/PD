package DataMessaging;

import java.io.Serializable;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

/**
* Mensagem entre Server-DirectoryService
*/
public class ServerMessage implements Serializable{
    static final long serialVersionUID = 1L;
    DataAddress server;
    List<DataAddress> users;
    String request;
    private Boolean exists;

    public ServerMessage(DataAddress server, List<DataAddress> users, String request, boolean exists) {
        this.setServer(server);
        this.setUsers(users);
        this.setRequest(request);
        this.setExists(exists);
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
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
    
    public String getServerName(){
        return this.server.getName();
    }
}