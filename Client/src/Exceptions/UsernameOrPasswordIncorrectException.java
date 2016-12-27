
package Exceptions;

/*
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class UsernameOrPasswordIncorrectException extends Exception{

    private String error;
    
    public UsernameOrPasswordIncorrectException(){
        this.error = "The username or password are incorrect.";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}
