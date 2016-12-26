
package DataMessaging;

import java.io.Serializable;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ClientServerMessage implements Serializable{

    static final long serialVersionUID = 1L;
    private Login login;
    private String request;
    private String filename;
    private String filePath;
    private boolean teste;
    
    public ClientServerMessage(){
        this.login = null;
        this.request = null;
        this.filePath = null;
        this.filename = null;
    }
    
    public void setLogin(Login login){
        this.login = login;
    }
    
    public Login getLogin(){
        return this.login;
    }
    
    public void setRequest(String request){
        this.request = request;
    }
    
    public void setTeste(boolean teste){
        this.teste = teste;
    }
    
    public boolean getTeste(){
        return this.teste;
    }
    
}
