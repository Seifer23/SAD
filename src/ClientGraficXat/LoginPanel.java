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

    private ChatPanel chatPanelInLogin;
    private JTextField user;
    private JButton submit;
    private JLabel username; 
    public MySocket mySocket;

    public LoginPanel(ChatPanel chatPanel) {
        super();
        chatPanelInLogin = chatPanel;

        user = new JTextField(20);
        submit = new JButton("Log in");
        username = new JLabel("Username: ");
        user.addActionListener(this);
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
}


