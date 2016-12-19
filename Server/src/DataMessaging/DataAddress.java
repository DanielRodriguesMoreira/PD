package DataMessaging;

import java.io.Serializable;
import java.net.InetAddress;

public class DataAddress implements Serializable{
    static final long serialVersionUID = 1L;
    String name;
    InetAddress ip;
    int port;
    long time;
    
    public DataAddress(String name, InetAddress ip, int port, long time){
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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