
package Exceptions;

import java.io.File;

/*
 * @author Daniel Moreira
 */

public class DirectoryNotExistsException extends Exception{

    private String error;
    
    public DirectoryNotExistsException(File directory){
        this.error = "Directory " + directory + " does not exist!";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}
