package ClientTextualXat;

import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.io.IOException;
import java.net.ServerSocket;

public class MyServerSocket {
    private ServerSocket serverSocket;

    public MyServerSocket(int port) {
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

    public MySocket accept() {
        try {
            Socket acceptedSocket = serverSocket.accept();
            return new MySocket(acceptedSocket);
        } catch (IOException e) {
            System.out.println("Error creating socket:" + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("The checkConnect method doesn't allow the operation:" + e.getMessage());
        } catch (IllegalBlockingModeException e) {
            System.out.println("Port parameter error: " + e.getMessage());
        }
        return null;
    }

}