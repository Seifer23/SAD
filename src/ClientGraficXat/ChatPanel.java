package ClientTextualXat;

import java.net.InetAddress;
import java.util.Arrays;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.Dimension;

public class ChatPanel extends JPanel 
                        implements ActionListener {
    
    final static String MENSAJE = "\\[m\\]";
    final static String CREAR = "\\[u\\]";
    final static String AGREGAR = "\\[n\\]";
    final static String ELIMINAR = "\\[d\\]";

    private JPanel inputPanel;
    private JSplitPane outputPanel;
    public JTextArea chatArea;
    private JScrollPane scrollPanelChat; 
    private JTextField inputMensaje;
    private JButton botonEnviar;
    public DefaultListModel<String> listaUsuarios;
    private JList<String> jListUsuarios;
    private JScrollPane scrollPanelUsuarios;
    private JLabel labelListaUsuarios;
    private JLabel labelChat;
    private String[] usuariosLogeados;
    private String usuario;
    private String mensaje;
    private String color;
    private MySocket mySocket;

    public ChatPanel() {
        super(new BorderLayout());

        //inputPanel
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.LINE_AXIS));
        inputMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");
        inputMensaje.addActionListener(this);
        botonEnviar.addActionListener(this);
        inputPanel.add(inputMensaje);
        inputPanel.add(botonEnviar);
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        //outputPanel
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        labelChat = new JLabel("Chat", SwingConstants.LEFT);
        scrollPanelChat = new JScrollPane(chatArea);
    
        listaUsuarios = new DefaultListModel<>();
        labelListaUsuarios = new JLabel("Usuarios conectados:", SwingConstants.RIGHT);
        jListUsuarios = new JList<>(listaUsuarios);
        scrollPanelUsuarios = new JScrollPane(jListUsuarios);
        scrollPanelUsuarios.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        outputPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPanelChat, scrollPanelUsuarios);
        outputPanel.setDividerLocation(0.7);
        outputPanel.setResizeWeight(0.7);

        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        outputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPanelChat.setPreferredSize(new Dimension(500, 300));
        scrollPanelChat.setMinimumSize(new Dimension(200, 300));
        scrollPanelUsuarios.setPreferredSize(new Dimension(300, 300));
        scrollPanelUsuarios.setMinimumSize(new Dimension(100, 300));
        
        this.add(labelChat);
        this.add(labelListaUsuarios);
        this.add(outputPanel,BorderLayout.CENTER);
        this.add(inputPanel,BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        String mensajeEnviar = inputMensaje.getText();
            if (!mensajeEnviar.isEmpty()) {
                mySocket.write(mensajeEnviar);
                chatArea.append("<Tu>"+mensajeEnviar+"\n");
                inputMensaje.setText("");
            }
    }

    public void listenForMessages(MySocket mySocket) {
        new Thread(() -> {
            try {
                this.mySocket = mySocket;
                String mensajeRecibido;
                while ((mensajeRecibido = mySocket.read()) != null) {
                    if(mensajeRecibido.charAt(1) == 'm'){
                        this.PrintMensaje(mensajeRecibido);
                    } else if(mensajeRecibido.charAt(1) == 'u'){
                        this.CrearListaUsuarios(mensajeRecibido);
                    } else{
                        this.ActualizarListaUsuarios(mensajeRecibido);
                    }    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void PrintMensaje(String mensajeRecibido){
        usuario = mensajeRecibido.substring(mensajeRecibido.indexOf("<")+1, mensajeRecibido.indexOf(">"));
        mensaje = mensajeRecibido.substring(mensajeRecibido.indexOf("0m")+2);
        chatArea.append("<"+usuario+">"+mensaje+"\n");

    }

    private void CrearListaUsuarios(String mensajeRecibido){
        usuariosLogeados = mensajeRecibido.replaceFirst(CREAR, "").replaceFirst("\n", "").split(",");
        Arrays.sort(usuariosLogeados);
        SwingUtilities.invokeLater(() -> {
            listaUsuarios.clear();
            for (String usuario : usuariosLogeados) {
                listaUsuarios.addElement(usuario);
            }
        });
    }

    private void ActualizarListaUsuarios(String mensajeRecibido){
        if (mensajeRecibido.charAt(1) == 'n') {
            listaUsuarios.addElement(mensajeRecibido.replaceFirst(AGREGAR, "").replaceFirst("\n", ""));
        }
        else {
            String usuarioEliminar = mensajeRecibido.replaceFirst(ELIMINAR, "").replaceFirst("\n", "");
            listaUsuarios.removeElement(usuarioEliminar);
        }
    }

    public JTextArea getChatArea() {
        return this.chatArea;
    }
}
