
package Exceptions;

/*
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class UsernameOrPasswordIncorrectException extends Exception{

    private String error;
    
    public UsernameOrPasswordIncorrectException(){
        this.error = "The username or password are incorrect.\nOr that account is used already.";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}
