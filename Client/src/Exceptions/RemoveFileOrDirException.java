
package Exceptions;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class RemoveFileOrDirException extends Exception{

    private String error;
    
    public RemoveFileOrDirException(){
        this.error = "It's impossible to remove the file. If it is a directory, make sure it is empty.";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}