


/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class UploadException extends Exception{

    private String error;
    
    public UploadException(){
        this.error = "Error writting the file.\nTry again later!";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}