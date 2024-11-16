package ClientTextualXat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MySocket{
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String username;

    public MySocket(InetAddress ip, int port, String username) {

        try {
            this.socket = new Socket(ip, port);
            this.printWriter = new PrintWriter(socket.getOutputStream(),true);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            write(this.username);
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

    public MySocket(Socket socket){
        this.socket = socket;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream(), true); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String read(){
        try{
            return bufferedReader.readLine();
        }catch(Exception e){
            return null;
        }
    }

    public void write(String str){
        printWriter.println(str);
    }

    public void close(){
        try {
            bufferedReader.close();
            printWriter.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
