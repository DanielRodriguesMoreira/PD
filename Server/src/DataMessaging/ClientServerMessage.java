
package DataMessaging;

import Constants.ClientServerRequests;
import static Constants.ClientServerRequests.CREATE_ACCOUNT;
import static Constants.ClientServerRequests.GET_WORKING_DIR_CONTENT;
import static Constants.ClientServerRequests.LOGIN;
import static Constants.ClientServerRequests.LOGOUT;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ClientServerMessage implements Serializable, ClientServerRequests{

    static final long serialVersionUID = 1L;
    private Login login;
    private String request;
    private boolean success;
    private DataAddress clientAddress;
    
    private String workingDirectoryPath;
    private ArrayList<File> workingDirectoryContent;
    private String pathToChange;
    
    public ClientServerMessage(){
        this.login = null;
        this.request = null;
        this.success = false;
        this.clientAddress = null;
        this.pathToChange = null;
    }
    
    /**
     * Prepare ClientServerMessage to Login the client in the server or to logout 
     * if the second param it's false
     *
     * @param login     the Login of the client
     * @param isToLogin true if is to login, false if is to logout
     * @param myAddress address of the client who sends the request
     */
    public ClientServerMessage(Login login, boolean isToLogin, DataAddress myAddress){
        this.login = login;
        this.clientAddress = myAddress;
        if(isToLogin){
            this.request = LOGIN;
        }else{
            this.request = LOGOUT;
        }
    }
    
    /**
     * Prepare ClientServerMessage to Create an account on the server
     *
     * @param login     the Login of the client
     * @param myAddress address of the client who sends the request
     */
    public ClientServerMessage(Login login, DataAddress myAddress){
        this.login = login;
        this.clientAddress = myAddress;
        this.request = CREATE_ACCOUNT;
    }
    
    /**
     * Prepate ClientServerMessage to get the content of working directory
     * 
     * @param myAddress address of the client who sends the request
     * @param getContent true if is to get the content of the working directory, false if is to get the working directory path
     */
    public ClientServerMessage(DataAddress myAddress, boolean getContent){
        this.clientAddress = myAddress;
        if(getContent){
            this.workingDirectoryContent = new ArrayList<>();
            this.request = GET_WORKING_DIR_CONTENT;
        }
        else{
            this.workingDirectoryPath = null;
            this.request = GET_WORKING_DIR_PATH;
        }
    }
    
    public ClientServerMessage(DataAddress myAddress, String path){
        this.clientAddress = myAddress;
        this.pathToChange = path;
        this.request = CHANGE_DIRECTORY;
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Get's ">
    public boolean getSuccess(){
        return this.success;
    }
    
    public void setSuccess(boolean success){
        this.success = success;
    }
    
    public String getRequest(){
        return this.request;
    }
    
    public Login getLogin(){
        return this.login;
    }
    
    public DataAddress getClientAddress(){
        return this.clientAddress;
    }
    
    public ArrayList<File> getWorkingDirContent(){
        return this.workingDirectoryContent;
    }
    
    public String getWorkingDirectoryPath(){
        return this.workingDirectoryPath;
    }
    
    public String getPathToChange(){
        return this.pathToChange;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Set's ">
    public void setWorkingDirectoryContent(ArrayList<File> files){
        this.workingDirectoryContent = files;
    }
    
    public void setWorkingDirectoryPath(String workingDirectoryPath){
        this.workingDirectoryPath = workingDirectoryPath;
    }
    // </editor-fold>
}
