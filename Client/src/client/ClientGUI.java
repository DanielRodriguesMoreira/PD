package client;

import Constants.Constants;
import DataMessaging.DataAddress;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListModel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */
public class ClientGUI extends JFrame implements Constants, Observer {

    static String username;
    static String ipAddress;
    static String portAddress;
    static int option;
    Client client;
    ArrayList<DataAddress> onlineServer = null;
    ArrayList<DataAddress> onlineClient = null;
    
    public ClientGUI() {
        initComponents();
        
        // <editor-fold defaultstate="collapsed" desc=" Mostrar InputDialog para escolher o nome/IPServerDirectory/PortServerDirectory ">
        do {
            JTextField field1 = new JTextField();
            JTextField field2 = new JTextField();
            JTextField field3 = new JTextField();
            field1.setText("Hugo");
            field2.setText("192.168.126.193");
            field3.setText("6000");

            Object[] message = {"Username:", field1, "SD Address:", field2, "SD Port:", field3};
            option = JOptionPane.showConfirmDialog(null, message, "Enter all your values", JOptionPane.OK_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                username = field1.getText();
                ipAddress = field2.getText();
                portAddress = field3.getText();
            }
            client = new Client(username, ipAddress, portAddress);
            this.client.addObserver(this);
        } while (username.isEmpty() || ipAddress.isEmpty() || portAddress.isEmpty() || option != JOptionPane.OK_OPTION || client.checkClientExists());
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Enviar HeartBeat para o serviceDirectory saber que estou vivo ">
        client.createHeartbeatThread();        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Enviar/Receber mensagem para atualizar as listas ">
        client.sendMessageToServiceDirectory(CLIENT_GET_ALL_LISTS);
        // </editor-fold>
        
        this.setTitle(username);
        this.setVisible(true);

       /* TESTE CONNECT SERVER
        try {
            client.connectServer(new DataAddress("daniel",InetAddress.getByName(ipAddress), 51126, -1));
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
            /*  3º GetOnlineServers()
            client.sendMessageToServiceDirectory(CLIENT_GET_ONLINE_SERVERS);
            /*  4º GetOnlineClients()
            client.sendMessageToServiceDirectory(CLIENT_GET_ONLINE_CLIENTS);
            5º GetAllLists()
            client.sendMessageToServiceDirectory(CLIENT_GET_ALL_LISTS);*/
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelServers = new javax.swing.JLabel();
        jLabelClients = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaMessages = new javax.swing.JTextArea();
        jButtonBroadcast = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jListServers = new javax.swing.JList<String>();
        jScrollPane6 = new javax.swing.JScrollPane();
        jListClients = new javax.swing.JList<String>();
        jButtonRefreshLists = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        jLabelServers.setText("Servers:");

        jLabelClients.setText("Clients:");

        jScrollPane3.setViewportView(jTree1);

        jTextAreaMessages.setEditable(false);
        jTextAreaMessages.setColumns(20);
        jTextAreaMessages.setRows(5);
        jScrollPane4.setViewportView(jTextAreaMessages);

        jButtonBroadcast.setText("Broadcast Message");
        jButtonBroadcast.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonBroadcastMouseClicked(evt);
            }
        });

        jListServers.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListServers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListServersMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jListServers);

        jListClients.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListClients.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListClientsMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(jListClients);

        jButtonRefreshLists.setText("Refresh Lists");
        jButtonRefreshLists.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonRefreshListsMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonBroadcast)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(63, 63, 63)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelServers)
                                    .addComponent(jLabelClients)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(jButtonRefreshLists)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jButtonRefreshLists)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelServers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabelClients)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonBroadcast)
                .addContainerGap(114, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jListServersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListServersMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() == 2) {
            System.out.println(this.onlineServer.get(this.jListServers.getSelectedIndex()).getIp());
            System.out.println(this.onlineServer.get(this.jListServers.getSelectedIndex()).getName());
            System.out.println(this.onlineServer.get(this.jListServers.getSelectedIndex()).getPort());
        }
    }//GEN-LAST:event_jListServersMouseClicked

    private void jButtonRefreshListsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRefreshListsMouseClicked
        // TODO add your handling code here:
        // <editor-fold defaultstate="collapsed" desc=" Enviar/Receber mensagem para atualizar as listas ">
        client.sendMessageToServiceDirectory(CLIENT_GET_ALL_LISTS);
        // </editor-fold>
    }//GEN-LAST:event_jButtonRefreshListsMouseClicked

    private void jListClientsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListClientsMouseClicked
        // TODO add your handling code here:
        if(!this.jListClients.isSelectionEmpty()){
            if(evt.getClickCount() == 2){
                DataAddress usernameToSend = this.onlineClient.get(this.jListClients.getSelectedIndex());
                String title = "Message to " + usernameToSend.getName();
                JFrame frame = new JFrame(title);
                
                String aux = JOptionPane.showInputDialog(frame, "Input your message", title, JOptionPane.OK_CANCEL_OPTION);
                String message = "[" + username + " - To " + usernameToSend.getName() + "] -> " + aux;
                if(!aux.isEmpty() || aux != null)
                    client.sendMessageToServiceDirectory(usernameToSend, message);
            }
        }
    }//GEN-LAST:event_jListClientsMouseClicked

    private void jButtonBroadcastMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonBroadcastMouseClicked
        // TODO add your handling code here:
        if(this.onlineClient.isEmpty())
            return;
        
        String title = "Message to All users";
        JFrame frame = new JFrame(title);
        String aux = JOptionPane.showInputDialog(frame, "Input your message", title, JOptionPane.OK_CANCEL_OPTION);
        String message = "[" + username + " - To All] -> " + aux;
        if(!aux.isEmpty() || aux != null)
            client.sendMessageToServiceDirectory(null, message);
    }//GEN-LAST:event_jButtonBroadcastMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBroadcast;
    private javax.swing.JButton jButtonRefreshLists;
    private javax.swing.JLabel jLabelClients;
    private javax.swing.JLabel jLabelServers;
    private javax.swing.JList<String> jListClients;
    private javax.swing.JList<String> jListServers;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextArea jTextAreaMessages;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables

    public void fillServersList() {
        if(client.getOnlineServers() == null ) return;
        this.onlineServer = new ArrayList<>(client.getOnlineServers());
        
        DefaultListModel<String> listServersModel = new DefaultListModel<String>();
        for(DataAddress da : onlineServer) {
            listServersModel.addElement(da.getName());
        }
        jListServers.setModel(listServersModel);
    }
    
    private void fillClientsList() {
        if(client.getOnlineClients() == null ) return;
        this.onlineClient = new ArrayList<>(client.getOnlineClients());
        
        DefaultListModel<String> listClientsModel = new DefaultListModel<String>();
        for(DataAddress da : onlineClient) {
            listClientsModel.addElement(da.getName());
        }
        jListClients.setModel(listClientsModel);
    }
    
    private void fillMessageTextArea() {
        if(this.client.getMessage() != null)
            this.jTextAreaMessages.setText(this.jTextAreaMessages.getText() + "\n" + client.getMessage());
    }

    @Override
    public void update(Observable o, Object arg) {
        fillClientsList();
        fillServersList();
        fillMessageTextArea();
        repaint();
    }
}
