package ClientTextualXat;

import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import java.awt.Dimension;

public class ChatPanel extends JPanel {
    
    private JPanel inputPanel;
    private JSplitPane outputPanel;
    public JTextArea chatArea;
    private JScrollPane scrollPanelChat; 
    private JTextField inputMensaje;
    private JButton botonEnviar;
    public DefaultListModel<String> listaUsuarios;
    private JList<String> jListUsuarios;
    private JScrollPane scrollPanelUsuarios;
    

    public ChatPanel() {
        super(new BorderLayout());

        //inputPanel
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.LINE_AXIS));
        inputMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");
        inputPanel.add(inputMensaje);
        inputPanel.add(botonEnviar);
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        //outputPanel
        chatArea = new JTextArea("chat");
        chatArea.setEditable(false);
        scrollPanelChat = new JScrollPane(chatArea);
        listaUsuarios = new DefaultListModel<>();
        //listaUsuarios.add(0,"a"); //QUITAAAAR
        jListUsuarios = new JList<>(listaUsuarios);
        scrollPanelUsuarios = new JScrollPane(jListUsuarios);
        chatArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        scrollPanelUsuarios.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        outputPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPanelChat, scrollPanelUsuarios);
        outputPanel.setDividerLocation(0.7);
        outputPanel.setResizeWeight(0.7);

        this.add(outputPanel,BorderLayout.CENTER);
        this.add(inputPanel,BorderLayout.SOUTH);
    }

    public void listenForMessages(MySocket mySocket) {
        new Thread(() -> {
            try {
                String mensaje;
                while ((mensaje = mySocket.read()) != null) {
                    String chatMensaje = mensaje.substring(1);
                    SwingUtilities.invokeLater(() -> chatArea.append(chatMensaje + "\n"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public JTextArea getChatArea() {
        return this.chatArea;
    }
}
