
package Exceptions;

/*
 * @author Daniel Moreira
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