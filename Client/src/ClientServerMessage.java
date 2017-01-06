

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
    private String newDirName;
    private String originalFilePath;
    private byte[] fileContent;
    
    /**
     * Default constructor
     */
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
     * @param password     the password of the client
     * @param isToLogin true if is to login, false if is to logout
     * @param myAddress address of the client who sends the request
     */
    public ClientServerMessage(String password, boolean isToLogin, DataAddress myAddress){
        this.login = new Login(myAddress.getName(), password);
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
    
    /**
     * Prepare ClientServerMessage to change working directory
     * 
     * @param myAddress address of the client who sends the request
     * @param newWorkingDir new Working dir
     */
    public ClientServerMessage(DataAddress myAddress, String newWorkingDir){
        this.clientAddress = myAddress;
        this.pathToChange = newWorkingDir;
        this.request = CHANGE_DIRECTORY;
    }
    
    /**
     * Prepare ClientServerMessage to make a dir or to remove a dir
     * 
     * @param myAddress address of the client who sends the request
     * @param fileOrDirName name of the file or directory to make/remove
     * @param isToMake true if is to make, false if is to remove
     */
    public ClientServerMessage(DataAddress myAddress, String fileOrDirName, boolean isToMake){
        this.clientAddress = myAddress;
        this.workingDirectoryContent = new ArrayList<>();
        this.newDirName = fileOrDirName;
        
        if(isToMake){
            this.request = MAKE_NEW_DIR;
        }else{
            this.request = REMOVE;
        }
    }
    
    /**
     * Prepare ClientServerMessage to copy&Paste
     * 
     * @param myAddress address of the client who sends the request
     * @param originalFilePath path of the file to copy
     * @param request
     */
    public ClientServerMessage(DataAddress myAddress, String originalFilePath, String request){
        this.clientAddress = myAddress;
        this.workingDirectoryContent = new ArrayList<>();
        this.originalFilePath = originalFilePath;
        
        this.request = request;
    }
    
    /**
     * Prepare ClientServerMessage to Upload a file to a server
     * 
     * @param myAddress address of the client who sends the request
     * @param fileContent byte array of the file to upload
     * @param fileName name of the file to upload
     */
    public ClientServerMessage(DataAddress myAddress, byte[] fileContent, String fileName){
        this.clientAddress = myAddress;
        this.fileContent = fileContent;
        this.newDirName = fileName;
        this.request = UPLOAD;
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
    
    public String getNewDirName(){
        return this.newDirName;
    }
    
    public String getOriginalFilePath(){
        return this.originalFilePath;
    }
    
    public byte[] getFileContent(){
        return this.fileContent;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Set's ">
    public void setWorkingDirectoryContent(ArrayList<File> files){
        this.workingDirectoryContent = files;
    }
    
    public void setWorkingDirectoryPath(String workingDirectoryPath){
        this.workingDirectoryPath = workingDirectoryPath;
    }
    
    public void setFileContent(byte[] fileContent){
        this.fileContent = fileContent;
    }
    // </editor-fold>
}
