
package DataMessaging;

/*
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ConfirmationMessage {

    private String serverName;
    private Boolean exists;
    
    public ConfirmationMessage(String serverName){
        this.serverName = serverName;
        this.exists = false;
    }
    
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Boolean serverExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }
    
}
