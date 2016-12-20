package DataMessaging;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class DataAddress implements Serializable, Comparable<DataAddress>{
    // <editor-fold defaultstate="collapsed" desc=" Variáveis ">
    static final long serialVersionUID = 1L;
    String name;
    InetAddress ip;
    int port;
    long time;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Construtor ">
    public DataAddress(String name, InetAddress ip, int port, long time){
        this.name = name;
        this.ip = ip;
        this.port = port;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gets & Sets ">
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" equals() ">
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.ip);
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
        final DataAddress other = (DataAddress) obj;
        if (!Objects.equals(this.name.toUpperCase(), other.name.toUpperCase())) {
            return false;
        }
        if (!Objects.equals(this.ip, other.ip)) {
            return false;
        }
        return true;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" compraTo(DataAddress t) ">
    @Override
    public int compareTo(DataAddress t) {
        if(this.equals(t)) 
            return 0;
        else 
            return 1;
    }
    // </editor-fold>
}