package client;

import Constants.Constants;
import DataMessaging.DataAddress;
import DataMessaging.Login;
import Exceptions.ClientNotLoggedInException;
import Exceptions.CreateAccountException;
import Exceptions.ServerConnectionException;
import Exceptions.UsernameOrPasswordIncorrectException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * @author Daniel Moreira
 * @author Hugo Santos
 * @author Tiago Santos 
 */
public class ClientGUI extends JFrame implements Constants, Observer {
    Client client;
    static String username;
    static String ipAddress;
    static String portAddress;
    static String password;
    static String passwordConfirmation;
    static int option;
    static boolean repeatDialogInput = false;
    static boolean cancelLoginCycle = false;
    static boolean cancelCreateAccountCycle = false;
    ArrayList<DataAddress> onlineServer = null;
    ArrayList<DataAddress> onlineClient = null;
    private javax.swing.JTree tree;
    DefaultMutableTreeNode root;
    public JPopupMenu popup;
    
    public ClientGUI() {
        initComponents();
        initTree();
        
        // <editor-fold defaultstate="collapsed" desc=" Mostrar InputDialog para escolher o nome/IPServerDirectory/PortServerDirectory ">
        do {
            JTextField inputUsernameTextField = new JTextField();
            JTextField inputIpAddressDirectoryServiceTextField = new JTextField();
            JTextField inputPortDirectoryServiceTextField = new JTextField();
            
            // <editor-fold defaultstate="collapsed" desc=" PARA TIRAR CONSTANTES NO INICIO ">
            inputUsernameTextField.setText("Hugo");
            inputIpAddressDirectoryServiceTextField.setText("localhost");
            inputPortDirectoryServiceTextField.setText("6000");
            // </editor-fold>
            

            Object[] message = {"Username:", inputUsernameTextField, 
                "SD Address:", inputIpAddressDirectoryServiceTextField, 
                "SD Port:", inputPortDirectoryServiceTextField};
            option = JOptionPane.showConfirmDialog(null, message, "Connect to Directory Service",
                     JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if(option == JOptionPane.OK_OPTION) {
                username = inputUsernameTextField.getText();
                ipAddress = inputIpAddressDirectoryServiceTextField.getText();
                portAddress = inputPortDirectoryServiceTextField.getText();
            } else
                System.exit(0);
            
            // <editor-fold defaultstate="collapsed" desc=" Create Client ">
            this.client = new Client(username, ipAddress, portAddress);
            this.client.addObserver(this);
            // </editor-fold>
            
        } while (username.isEmpty() || ipAddress.isEmpty() || portAddress.isEmpty() || option != JOptionPane.OK_OPTION || client.checkClientExists());
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Enviar/Receber mensagem para atualizar as listas ">
        client.getAllLists();
        // </editor-fold>
        
        this.setTitle(username);
        this.setVisible(true);
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
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaMessages = new javax.swing.JTextArea();
        jButtonBroadcast = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jListServers = new javax.swing.JList<>();
        jScrollPane6 = new javax.swing.JScrollPane();
        jListClients = new javax.swing.JList<>();
        jButtonRefreshLists = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        jLabelServers.setText("Servers:");

        jLabelClients.setText("Clients:");

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

        jListServers.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jListServers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListServersMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jListServers);

        jListClients.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
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
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))
                    .addGroup(layout.createSequentialGroup()
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
                        .addContainerGap(114, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void initTree(){
        root = new DefaultMutableTreeNode("Root");
        tree = new javax.swing.JTree(root);
        jScrollPane1.setViewportView(tree);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Events ">
    private void jListServersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListServersMouseClicked
        // TODO add your handling code here:
        if(!this.jListServers.isSelectionEmpty()) {
            if (evt.getButton() == 3) {
                popup = new JPopupMenu();
                JMenuItem itemLogin, itemLogout, itemRegister;
                // <editor-fold defaultstate="collapsed" desc=" Login Item (PopUpMenu) ">
                itemLogin = new JMenuItem("Login");
                itemLogin.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        do {
                            repeatDialogInput = false;
                            cancelLoginCycle = false;
                            // <editor-fold defaultstate="collapsed" desc=" Mostrar InputDialog para escolher o username e password ">
                            do {
                                JTextField inputUsernameTextField = new JTextField();
                                JTextField inputPasswordTextField = new JTextField();

                                Object[] message = {"Username:", inputUsernameTextField, "Password:", inputPasswordTextField};
                                option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                                if(option == JOptionPane.OK_OPTION) {
                                    username = inputUsernameTextField.getText();
                                    password = inputPasswordTextField.getText();
                                } else if(option == JOptionPane.CANCEL_OPTION) {
                                    cancelLoginCycle = true;
                                    break;
                                }
                            } while (username.isEmpty() || password.isEmpty());
                            // </editor-fold>
                            
                            if (cancelLoginCycle == false) {
                                try {
                                client.Login(new Login(username, password), onlineServer.get(jListServers.getSelectedIndex()));
                                } catch (ServerConnectionException | UsernameOrPasswordIncorrectException ex) {
                                    repeatDialogInput = true;
                                    JOptionPane.showConfirmDialog(rootPane, ex, "Login error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                                } catch (ClientNotLoggedInException | CreateAccountException ex) {}
                            }
                        } while(repeatDialogInput);
                        DefaultMutableTreeNode server = new DefaultMutableTreeNode(onlineServer.get(jListServers.getSelectedIndex()).getName());
                        root.add(server);
                        addFiles(client.getWorkingDirContent(null, onlineServer.get(jListServers.getSelectedIndex())), server);
                    }
                });
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" New Account Item (PopUpMenu) ">
                itemRegister = new JMenuItem("New account");
                itemRegister.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // <editor-fold defaultstate="collapsed" desc=" Mostrar InputDialog para escolher o username e password ">
                        do {
                            JTextField inputUsernameTextField = new JTextField();
                            JTextField inputPasswordTextField = new JTextField();
                            JTextField inputPasswordAgainTextField = new JTextField();

                            Object[] message = {"Username:", inputUsernameTextField, 
                                                "Password:", inputPasswordTextField,
                                                "Confirm Password:", inputPasswordAgainTextField};
                            option = JOptionPane.showConfirmDialog(null, message, "Create New Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                            if (option == JOptionPane.OK_OPTION) {
                                username = inputUsernameTextField.getText();
                                password = inputPasswordTextField.getText();
                                passwordConfirmation = inputPasswordAgainTextField.getText();
                            } else if (option == JOptionPane.CANCEL_OPTION) {
                                cancelCreateAccountCycle = true;
                            }
                            if(!password.equals(passwordConfirmation))
                               JOptionPane.showConfirmDialog(rootPane, "Palavra pass não coincide", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                        } while (username.isEmpty() || password.isEmpty() || !password.equals(passwordConfirmation));
                        // </editor-fold>
                        
                        if (cancelCreateAccountCycle == false) {
                            try {
                                client.CreateAccount(new Login(username, password), onlineServer.get(jListServers.getSelectedIndex()));
                            } catch (ServerConnectionException | CreateAccountException ex) {
                                JOptionPane.showConfirmDialog(rootPane, ex, "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                            } catch (UsernameOrPasswordIncorrectException | ClientNotLoggedInException ex) {/*ignore*/}
                        }
                    }
                });
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc=" Logout Item (PopUpMenu) ">
                itemLogout =  new JMenuItem("Logout");
                itemLogout.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            client.Logout(new Login(username, password), onlineServer.get(jListServers.getSelectedIndex()));
                        } catch (ServerConnectionException | ClientNotLoggedInException ex) {
                           JOptionPane.showConfirmDialog(rootPane, ex, "Logout error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                        }catch (UsernameOrPasswordIncorrectException | CreateAccountException ex) {/*ignorar*/}
                    }
                });
                // </editor-fold>
                
                // <editor-fold defaultstate="collapsed" desc=" Adicionar itens ao PopUpMenu/ Mostrar PopUpMenu ">
                popup.add(itemLogin);
                popup.add(itemRegister);
                popup.add(itemLogout);
                popup.show(this.jListServers, evt.getX(), evt.getY());
                // </editor-fold>
            }
        }
    }//GEN-LAST:event_jListServersMouseClicked

    private void jButtonRefreshListsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRefreshListsMouseClicked
        // TODO add your handling code here:
        // <editor-fold defaultstate="collapsed" desc=" Enviar/Receber mensagem para atualizar as listas ">
        client.getAllLists();
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
                    client.sendMessageTo(usernameToSend, message);
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
            client.sendMessageToAll(message);
    }//GEN-LAST:event_jButtonBroadcastMouseClicked
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Variables declaration - do not modify ">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBroadcast;
    private javax.swing.JButton jButtonRefreshLists;
    private javax.swing.JLabel jLabelClients;
    private javax.swing.JLabel jLabelServers;
    private javax.swing.JList<String> jListClients;
    private javax.swing.JList<String> jListServers;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextArea jTextAreaMessages;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
    
    private void addFiles(File [] files, DefaultMutableTreeNode node) {
        
        for(File f: files){
            node.add( new DefaultMutableTreeNode(f.getName()));
        }
    }
    
    private void fillServersList() {
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
