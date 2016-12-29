
package Exceptions;

/*
 * @author Daniel Moreira
 */

public class MakeDirException extends Exception{

    private String error;
    
    public MakeDirException(){
        this.error = "It's impossible to create that directory.\nTry again later.";
    }
    
    @Override
    public String toString(){
        return this.error;
    }
}