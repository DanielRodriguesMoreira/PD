package DataMessaging;

import java.io.Serializable;
import java.util.List;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class ClientMessage implements Serializable {
    static final long serialVersionUID = 1L;
    DataAddress dataAddress;         //DataAddress para fazer comunicação
    String usernameToSend;          // Nome do Cliente que quer enviar a mensagem
    String message;                 // Mensagem para um Cliente
    String request;                 // Pedido ({"sendMessage","sendMessageToAll","imAlive"}) para Directoria; ({"updateLists","recieveMessage"}) para o Cliente;
    List<DataAddress> listServers;  // Lista dos servidores
    List<String> listClients;       // Lista dos Clientes Ativos
    boolean exists;                 // Ver se cliente já existe

    public ClientMessage(DataAddress dataAddress, String usernameToSend, String message, String request, List<DataAddress> listServers, List<String> listClients, boolean exists) {
        this.dataAddress = dataAddress;
        this.usernameToSend = usernameToSend;
        this.message = message;
        this.request = request;
        this.listServers = listServers;
        this.listClients = listClients;
        this.exists = exists;
    }

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

    public String getUsernameToSend() {
        return usernameToSend;
    }

    public void setUsernameToSend(String usernameToSend) {
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
        this.listServers = listServers;
    }

    public List<String> getListClients() {
        return listClients;
    }

    public void setListClients(List<String> listClients) {
        this.listClients = listClients;
    } 
}