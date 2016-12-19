package DataMessaging;

import java.io.Serializable;
import java.net.InetAddress;

public class DataAddress implements Serializable{
    static final long serialVersionUID = 1L;
    String name;
    InetAddress ip;
    int port;
    
    public DataAddress(String name, InetAddress ip, int port){
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}