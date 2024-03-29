


/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class CopyFileException extends Exception{

    private String error;
    
    public CopyFileException(String filename){
        this.error = "It's impossible to copy and paste the next file: " + filename + ".\nTry again later.";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}