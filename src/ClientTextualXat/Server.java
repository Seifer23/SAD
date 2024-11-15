package ClientTextualXat;

public class Server {
    public static final int PORT = 8080;
    public static void main(String[] args) {

        MyServerSocket serverSocket = new MyServerSocket(PORT);
        UsersHashMap userMap = new UsersHashMap();
        System.out.println("Comienzo de chat: Esperando usuarios...");

        while(true){
            MySocket sock = serverSocket.accept();
            new Thread(){
                public void run(){
                    
                    String username = sock.read();
                    String nickname = userMap.addUser(username, sock);
                    System.out.println(nickname + " s'ha connectat");
                    String clientText;
                    while((clientText=sock.read())!=null){
                        userMap.broadcast(nickname,clientText);
                        System.out.println(nickname + ": " + clientText);
                    }
                    System.out.println(nickname + "ha marxat del xat");
                    userMap.removeUser(username);
                    sock.close();
                }
            }.start();
        }
    }
}
