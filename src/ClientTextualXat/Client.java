package ClientTextualXat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import EditableBufferedReader.EscapeSeq;

public class Client {

    public static String username;

    public static void main(String[] args) {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));
        System.out.print(EscapeSeq.CLEAR);
        System.out.print("Username:");
        try{
            username = in.readLine();
        } catch(Exception e){   
            System.out.println("Error while reading username");
        }

        final MySocket mySock;
        try {
            mySock = new MySocket(InetAddress.getByName("127.0.0.1"), 8080, username);
        } catch (UnknownHostException e) {
            System.out.println("Error while connecting to the server");
            return;
        }

        Thread writeThread = new Thread(() -> {
            try{
                String line;
                while ((line = in.readLine()) != null){
                    mySock.write(line);                                        
                }
            }catch (Exception e){
                System.out.println("Error while reading username");
            }
        });
        writeThread.start();

        Thread readThread = new Thread(() -> {

            try{
                String line;
                while((line = mySock.read()) != null){
                    System.out.println(line);
                }
            }catch(Exception e){
                System.out.println("Error while reading ");
            }
        });

        readThread.start();

    }
}