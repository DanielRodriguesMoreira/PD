

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ClientMessage implements Serializable {
    // <editor-fold defaultstate="collapsed" desc=" Variáveis ">
    static final long serialVersionUID = 1L;
    DataAddress dataAddress;         //DataAddress para fazer comunicação
    DataAddress usernameToSend;          // Nome do Cliente que quer enviar a mensagem
    String message;                 // Mensagem para um Cliente
    String request;                 // Pedido ({"sendMessage","sendMessageToAll","imAlive"}) para Directoria; ({"updateLists","recieveMessage"}) para o Cliente;
    List<DataAddress> listServers;  // Lista dos servidores
    List<DataAddress> listClients;  // Lista dos Clientes Ativos
    boolean exists;                 // Ver se cliente já existe
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Construtor ">
    public ClientMessage(DataAddress dataAddress, DataAddress usernameToSend, String message, String request, List<DataAddress> listServers, List<DataAddress> listClients, boolean exists) {
        this.dataAddress = dataAddress;
        this.usernameToSend = usernameToSend;
        this.message = message;
        this.request = request;
        this.listServers = new ArrayList<>();
        this.listClients = new ArrayList<>();
        this.exists = exists;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gets & Sets ">
    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public DataAddress getDataAddress() {
        return dataAddress;
    }

    public void setDataAddress(DataAddress dataAddress) {
        this.dataAddress = dataAddress;
    }

    public DataAddress getUsernameToSend() {
        return usernameToSend;
    }

    public void setUsernameToSend(DataAddress usernameToSend) {
        this.usernameToSend = usernameToSend;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public List<DataAddress> getListServers() {
        return listServers;
    }

    public void setListServers(List<DataAddress> listServers) {
        this.listServers = new ArrayList<>(listServers);
    }

    public List<DataAddress> getListClients() {
        return listClients;
    }

    public void setListClients(List<DataAddress> listClients) {
        this.listClients = new ArrayList<>(listClients);
    }
    // </editor-fold>
}