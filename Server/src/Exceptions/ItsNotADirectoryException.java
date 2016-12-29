
package Exceptions;

import java.io.File;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ItsNotADirectoryException extends Exception{

    private String error;
    
    public ItsNotADirectoryException(File directory){
        this.error = "The path " + directory + " does not refer to a directory!";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}
