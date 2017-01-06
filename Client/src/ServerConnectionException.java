


/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ServerConnectionException extends Exception{

    private String error;
    
    public ServerConnectionException(String error){
        this.error = error;
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}