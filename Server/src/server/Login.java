
package server;

import java.util.Objects;

/*
 * @author Daniel Moreira
 */

public class Login {
    private String username;
    private String password;
    
    public Login(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }
    
    @Override
    public String toString(){
        return "Username: " + this.getUsername() + "\nPassword: " + this.getPassword();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.username);
        hash = 17 * hash + Objects.hashCode(this.password);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Login other = (Login) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        return true;
    }
    
    public boolean equals(String username){
        if(username == null){
            return false;
        }
        
        if(!this.getUsername().equalsIgnoreCase(username)){
            return false;
        }
        
        return true;
    }
    
}
