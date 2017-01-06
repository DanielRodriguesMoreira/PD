


/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ClientNotLoggedInException extends Exception{

    private String error;
    
    public ClientNotLoggedInException(){
        this.error = "First you need to login!";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}