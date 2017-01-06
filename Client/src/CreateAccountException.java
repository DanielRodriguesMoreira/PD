


/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */
public class CreateAccountException extends Exception{

    private String error;
    
    public CreateAccountException(){
        this.error = "Error creating account. Try again later!";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}