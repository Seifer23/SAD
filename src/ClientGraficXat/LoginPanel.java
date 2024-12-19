package ClientTextualXat;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class LoginPanel extends JPanel
        implements ActionListener {

    final static String LOGINPANEL = "login_panel";
    final static String CHATPANEL = "chat_panel";

    final static String CREAR = "u]";

    private ChatPanel chatPanelInLogin;
    private JTextField user;
    private JButton submit;
    private JLabel username; 
    private List<String> usuariosLogeados;
    public MySocket mySocket;
    private DefaultListModel<String> listaUsuarios;

    public LoginPanel(ChatPanel chatPanel) {
        super();
        chatPanelInLogin = chatPanel;
        listaUsuarios = chatPanel.listaUsuarios;

        user = new JTextField(20);
        submit = new JButton("Log in");
        username = new JLabel("Username: ");
        submit.addActionListener(this);
        
        this.add(username);
        this.add(user);
        this.add(submit);
    }

    public void actionPerformed(ActionEvent e) {
        String userInput = user.getText().trim();
        if (userInput.isEmpty()) {
            return;
        }
        initConnect(userInput);
        new Thread(() -> RecepcionMensaje()).start();
    }

    private void initConnect(String userInput) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    mySocket = new MySocket(InetAddress.getByName("127.0.0.1"), 8080, userInput);
                    System.out.println("Connection established with: " + mySocket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void done() {
                try {
                    get(); 
                    SwingUtilities.invokeLater(() -> {
                        chatPanelInLogin.listenForMessages(mySocket); 
                        //RESOLLVEEEEEEEEER
                        CardLayout cl = (CardLayout) getParent().getLayout();
                        cl.show(getParent(), ClientGrafic.CHATPANEL);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void RecepcionMensaje() {
        String mensajeRecibido;
        while ((mensajeRecibido = this.mySocket.read()) != null) {
            if(mensajeRecibido.charAt(0) == 'm'){
                this.PrintMensaje();
            } else if(mensajeRecibido.charAt(0) == 'u'){
                this.CrearListaUsuarios(mensajeRecibido);
            } else{
                this.ActualizarListaUsuarios();
            }    
        }
    }

    private void PrintMensaje(){

    }

    private void CrearListaUsuarios(String mensajeRecibido){
   
    }

    private void ActualizarListaUsuarios(){

    }

    public MySocket getLoginSocket() {
        return this.mySocket;
    }

}
