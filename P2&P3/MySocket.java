import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MySocket{
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public MySocket(InetAddress address, int port) {
        try {
            this.socket = new Socket(address, port);
        } catch (IOException e) {
            System.out.println("Error creating socket:" + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("The checkConnect method doesn't allow the operation:" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Port parameter error: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Address is null: " + e.getMessage());
        }
    }
}
