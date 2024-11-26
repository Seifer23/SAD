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
                    System.out.println("[n]" + nickname);
                    String clientText;
                    while((clientText=sock.read())!=null){
                        userMap.sendMessage(nickname,clientText);
                        System.out.println("[m]<"+ nickname + "> " + clientText);
                    }
                    System.out.println("[d]" + nickname);
                    userMap.removeUser(nickname);
                    sock.close();
                }
            }.start();
        }
    }
}
