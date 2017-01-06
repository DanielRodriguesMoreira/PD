


import java.io.File;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class DirectoryPermissionsDeniedException extends Exception{

    private String error;
    
    public DirectoryPermissionsDeniedException(File directory){
        this.error = "No write permissions in directory " + directory;
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}