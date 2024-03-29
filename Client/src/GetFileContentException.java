


/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class GetFileContentException extends Exception{

    private String error;
    
    public GetFileContentException(){
        this.error = "Error getting the file content.\nTry again later!";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}