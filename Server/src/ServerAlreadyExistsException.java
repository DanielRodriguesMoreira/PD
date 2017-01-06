


/*
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ServerAlreadyExistsException extends Exception{

    private String error;
    
    public ServerAlreadyExistsException(String serverName){
        this.error = "The '" + serverName + "' already exists in Directory Service";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}
