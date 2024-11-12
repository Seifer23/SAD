package ClientTextualXat;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class MyServerSocket {
    private ServerSocket serverSocket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public MyServerSocket(int port){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error creating socket:" + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("The checkConnect method doesn't allow the operation:" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Port parameter error: " + e.getMessage());
        }
    }


}
