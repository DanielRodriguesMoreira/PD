


import java.io.File;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
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
