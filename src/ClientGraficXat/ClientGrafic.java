package ClientTextualXat;

import java.net.InetAddress;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class ClientGrafic {

    final static String LOGINPANEL = "login_panel";
    final static String CHATPANEL = "chat_panel";

    private MyFrame myFrame;
    public JPanel mainPanel;
    public CardLayout cardLayout;
    private LoginPanel loginPanel;
    private ChatPanel chatPanel;
    private MySocket mySocket;
    private JTextArea chatArea;

    public ClientGrafic() {
        myFrame = new MyFrame();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        chatPanel = new ChatPanel();
        loginPanel = new LoginPanel(chatPanel);

        mySocket = loginPanel.getLoginSocket();
        chatArea = chatPanel.getChatArea();
        // listenForMessages(mySocket);
    
        mainPanel.add(loginPanel,LOGINPANEL);
        mainPanel.add(chatPanel,CHATPANEL);
        
        cardLayout.show(mainPanel,LOGINPANEL);
        myFrame.add(mainPanel);
                
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        JFrame.setDefaultLookAndFeelDecorated(true);  
        ClientGrafic clientGrafic = new ClientGrafic();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {      
                createAndShowGUI();
            }
        });
    }

    class MyFrame extends JFrame {
        public MyFrame() {
            super();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            JFrame.setDefaultLookAndFeelDecorated(true);
            this.setSize(750, 750);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        }
    }
}

