
package Exceptions;

/*
 * @author Daniel Moreira
 */

public class WriteOnFileException extends Exception{

    private String error;
    
    public WriteOnFileException(String filename, String defaultError){
        this.error = "Error writing on " + filename + " file.\n(" + defaultError + ")";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}