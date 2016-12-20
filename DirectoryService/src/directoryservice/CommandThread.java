/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package directoryservice;

import DataMessaging.DataAddress;
import static directoryservice.DirectoryService.mapServers;
import static directoryservice.DirectoryService.listClients;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */
public class CommandThread extends Thread{
    
    @Override
    public void run(){
        String cmd;
        do{
            Scanner reader = new Scanner(System.in);
            System.out.print("Command: ");
            cmd = reader.nextLine();
            switch(cmd){
                // <editor-fold defaultstate="collapsed" desc=" listservers ">
                case "listservers":
                    List<DataAddress> listServers = new ArrayList<>(mapServers.keySet());
                    if(listServers.size() > 0){
                         System.out.println("List of Servers:");
                        for(DataAddress i : listServers)
                            System.out.println("Name: " + i.getName() + " IP: " + i.getIp().getHostName() + " Port: " + i.getPort() +" Time: " + i.getTime());
                    } else 
                        System.out.println("No Server Active.");
                    break;
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" listclients ">
                case "listclients":
                    if(listClients.size() > 0){
                        System.out.println("List of Clients:");
                        for(DataAddress i : listClients)
                            System.out.println("Name: " + i.getName() + " IP: " + i.getIp().getHostName() + " Port: " + i.getPort() +" Time: " + i.getTime());
                    } else
                        System.out.println("No Client Active.");
                    break;
                // </editor-fold>
             }
        }while(!cmd.equals("close"));
    }
}