
package DataMessaging;

import Constants.ClientServerRequests;
import java.io.File;
import java.io.Serializable;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ClientServerMessage implements Serializable, ClientServerRequests{

    static final long serialVersionUID = 1L;
    private Login login;
    private String request;
    private String filename;
    private String filePath;
    private boolean success;
    private DataAddress clientAddress;
    private String dirPath;
    private File[] dirContent;
    
    public ClientServerMessage(){
        this.login = null;
        this.request = null;
        this.filePath = null;
        this.filename = null;
        this.success = false;
        this.clientAddress = null;
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
    
    public ClientServerMessage(String path, DataAddress myAddress){
        this.clientAddress = myAddress;
        this.dirPath = path;
        this.request = GET_WORKING_DIR_CONTENT;
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
    
    public File[] getWorkingDirContent(){
        return this.dirContent;
    }
    
    public String getDirPath(){
        return this.dirPath;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Set's ">
    public void setDirContent(File[] files){
        this.dirContent = files;
    }
    // </editor-fold>
}
