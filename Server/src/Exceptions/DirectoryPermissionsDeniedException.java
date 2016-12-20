
package Exceptions;

import java.io.File;

/*
 * @author Daniel Moreira
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