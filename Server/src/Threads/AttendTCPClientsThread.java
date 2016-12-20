
package Threads;

import Constants.Constants;
import DataMessaging.DataAddress;
import DataMessaging.ServerMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import server.FilesInterface;
import server.Login;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */

public class AttendTCPClientsThread extends Thread implements Constants, FilesInterface{

    private Socket toClientSocket = null;
    private DataAddress myAddress = null;
    private InetAddress directoryServiceIP = null;
    private int directoryServicePort = -1;
    private List<DataAddress> usersLoggedIn = null;
    private List<Login> loginsList = null;
    private File rootDirectory = null;
    private String loginFile = null;
    
    public AttendTCPClientsThread(Socket socket, DataAddress myAddress, InetAddress dsIP, int dsPort, List<DataAddress> users, 
            List<Login> loginsList, File rootDirectory, String loginFile){
        this.toClientSocket = socket;
        this.myAddress = myAddress;
        this.directoryServiceIP = dsIP;
        this.directoryServicePort = dsPort;
        this.usersLoggedIn = users;
        this.loginsList = loginsList;
        this.rootDirectory = rootDirectory;
        this.loginFile = loginFile;
    }
    
    @Override
    public void run(){
        
        while(true){
            //Vou recebendo pedidos
            
            //se for x faz x
            //se for y faz y
            //....

        }
        
    }
   
    private boolean usernameAlreadyExists(Login login){
        for(Login l : this.loginsList){
            if(login.equals(login.getUsername()))
                return true;
        }
        
        return false;
    }
    
    private boolean isUsernameAndPasswordCorrect(Login login){
        for(Login l : this.loginsList){
            if(login.equals(l))
                return true;
        }
        
        return false;
    }
    
    private void addUserToList(DataAddress userAddress){
        synchronized(this.usersLoggedIn){
            this.usersLoggedIn.add(userAddress);
        }
        this.notifyDirectoryServiceAboutUsersList();
    }
    
    private void removeUserFromList(DataAddress userAddress){
        boolean isToNofify = false;
        
        synchronized(this.usersLoggedIn){
            for(DataAddress dataAddress : this.usersLoggedIn){
                if(dataAddress.equals(userAddress)){
                    this.usersLoggedIn.remove(userAddress);
                    isToNofify = true;
                    break;
                }
            }
        }
        
        if(isToNofify){
            this.notifyDirectoryServiceAboutUsersList();
        }
    }
    
    /**
     * Este método vai ser chamado em 2 situações:
     *      -   adduserToList
     *      -   removeUserFromList
     */
    private void notifyDirectoryServiceAboutUsersList(){
        
        try {
            
            DatagramSocket socketUDP = new DatagramSocket();
            
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();            
            ObjectOutputStream out = new ObjectOutputStream(bOut);

            ServerMessage serverMessage = new ServerMessage(this.myAddress, this.usersLoggedIn, SERVER_MSG_UPDATE_LIST, false);
            
            out.writeObject(serverMessage);
            out.flush();
            
            DatagramPacket packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), this.directoryServiceIP, this.directoryServicePort);
            socketUDP.send(packet);
            
        } catch (SocketException ex) {
            System.out.println("An error occurred with the UDP socket level:\n\t" + ex);
        } catch (IOException ex) {
            System.out.println("An error occurred in accessing the socket:\n\t" + ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc=" Methods from FilesInterface ">
    @Override
    public boolean Login(Login login) {
        if(isUsernameAndPasswordCorrect(login))
            return true;
        
        return false;
    }

    @Override
    public boolean Logout(Login login) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean CreateAccount(Login login, File rootDirectory) {
        //1º verificar se já existe
        if(this.usernameAlreadyExists(login))
            return false;

        //2º adicionar ao ficheiro de texto
        
        //3º adicionar à lista para avisar o tiago
        //4º criar pasta raiz para esse username
        //5º enviar pasta ao cliente?!?!?
        
        return true;
    }
    // </editor-fold>
}
