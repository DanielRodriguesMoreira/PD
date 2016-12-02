package DataMessaging;

import java.net.InetAddress;

public class DataAddress {
    String name;
    InetAddress ip;
    int port;
    
    public DataAddress(String name, InetAddress ip, int port){
        this.name = name;
        this.ip = ip;
        this.port = port;
    }
    
    public String getName(){
        return this.name;
    }
    
    public InetAddress getIP(){
        return this.ip;
    }
    
    public int getPort(){
        return this.port;
    }
}