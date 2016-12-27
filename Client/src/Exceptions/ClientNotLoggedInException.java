
package Exceptions;

/*
 * @author Daniel Moreira
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