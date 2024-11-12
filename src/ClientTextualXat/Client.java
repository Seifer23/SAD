package ClientTextualXat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//TODO: Editar EditableBufferedReader per permetre implementar-lo i que reconegui que la posició inicial no és 0

//import EditableBufferedReader.EditableBufferedReader;
import EditableBufferedReader.EscapeSeq;

public class Client {

    public static String username;

    public static void main(String[] args) {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));
        System.out.print(EscapeSeq.CLEAR);
        System.out.print("Nom d'usuari:");
        try{
            username = in.readLine();
        } catch(Exception e){   
            System.out.println("Error en la lectura");
        }

        MySocket mySock = new MySocket("127.0.0.1", 8080, username);

    }
}