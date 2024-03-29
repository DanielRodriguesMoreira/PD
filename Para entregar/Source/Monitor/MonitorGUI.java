
import java.awt.Font;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author drmor
 */
public class MonitorGUI extends javax.swing.JFrame implements Observer{

    private Monitor monitor;
    
    public MonitorGUI() {
        initComponents();
        this.setMinimumSize(new java.awt.Dimension(1000, 1000));
        this.setPreferredSize(new java.awt.Dimension(1000, 1000));
        // <editor-fold defaultstate="collapsed" desc=" Centrar janela ">
        java.awt.Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Mostrar input do IP/Port do Serviço de Directoria ">
        int option = -1;
        String ipAddress = null;
        String portAddress = null;
        do {
            JTextField inputIpAddressDirectoryServiceTextField = new JTextField();
            JTextField inputPortDirectoryServiceTextField = new JTextField();   

            Object[] message = 
            {
                "SD Address:", inputIpAddressDirectoryServiceTextField, 
                "SD Port:", inputPortDirectoryServiceTextField
            };
            
            option = JOptionPane.showConfirmDialog(this, message, "Connect to Directory Service",
                     JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if(option == JOptionPane.OK_OPTION) {
                ipAddress = inputIpAddressDirectoryServiceTextField.getText();
                portAddress = inputPortDirectoryServiceTextField.getText();
            } else
                System.exit(0);
        } while (ipAddress.isEmpty() || portAddress.isEmpty() || option != JOptionPane.OK_OPTION);
        // </editor-fold>
        
        try {
            this.monitor = new Monitor(ipAddress, Integer.parseInt(portAddress));
            this.monitor.addObserver(this);
            
            this.setVisible(true);
        } catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "SD Port should be a number!", "Number Format Exception", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Servers Monitoring");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("List of servers and authenticated clients:");
        jLabel1.setMaximumSize(new java.awt.Dimension(283, 50));
        jLabel1.setMinimumSize(new java.awt.Dimension(283, 50));
        jLabel1.setPreferredSize(new java.awt.Dimension(283, 50));
        getContentPane().add(jLabel1, java.awt.BorderLayout.PAGE_START);

        jMenuBar1.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        jMenu1.setText("File");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jMenuItem2.setText("About");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        this.monitor.exit();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        String title = "Distributed File System - Monitor RMI";
        String message = "<html>Work done in the Distributed Programming class (Prof. José Marinho) in the first semester<br/>"
                + "of the 3rd year of the ISEC Computer Engineering course(2016/2017).<br/>"
                + "Authors:<br/>"
                + "&#9Daniel Moreira&#9Nº21240321<br/>"
                + "&#9Hugo Santos&#9Nº21220702<br/>"
                + "&#9Tiago Santos&#9Nº21230530";
        JLabel txtMessage = new JLabel(message);
        txtMessage.setFont(new Font("Tahoma", Font.PLAIN, 22));
        JOptionPane.showMessageDialog(null, txtMessage, title, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    // <editor-fold defaultstate="collapsed" desc=" Variables declaration - do not modify ">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    @Override
    public void update(Observable o, Object o1) {
        this.jTextArea1.setText(this.monitor.getServersInformation());
    }
}
