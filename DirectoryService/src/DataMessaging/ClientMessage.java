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
    String user;                    // Nome do Cliente que envia
    String usernameToSend;          // Nome do Cliente que quer enviar a mensagem
    String message;                 // Mensagem para um Cliente
    String request;                 // Pedido ({"sendMessage","sendMessageToAll","imAlive"}) para Directoria; ({"updateLists","recieveMessage"}) para o Cliente;
    List<DataAddress> listServers;  // Lista dos servidores
    List<String> listClients;       // Lista dos Clientes Ativos

    public ClientMessage(String user, String usernameToSend, String message, String request, List<DataAddress> listServers, List<String> listClients) {
        this.user = user;
        this.usernameToSend = usernameToSend;
        this.message = message;
        this.request = request;
        this.listServers = listServers;
        this.listClients = listClients;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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