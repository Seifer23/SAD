package ClientTextualXat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class UsersHashMap {

    Map<String, MySocket> userMap;
    public UsersHashMap() {
        userMap = new ConcurrentHashMap<>();
    }

    public String addUser(String username, MySocket sock){
        String nick = null;
        if(userMap.containsKey(username)){
            do{
                nick = username + (int) (Math.random() * 10);
            } while (userMap.containsKey(nick));
        } else {
            nick = username;
        }
        userMap.put(nick, sock);
        return nick;
    }

    public void broadcast(String sender, String message){

      int senderColor = 31 + (sender.toLowerCase().charAt(0) - 'a') % 6;
        for(String user : userMap.keySet()){
            if(!user.equals(sender))
                userMap.get(user).write("\033["+ senderColor +"m<" + sender + ">\033[0m" + message);
        }
    }

    public void send(String reciever, String message){
        userMap.get(reciever).write(message);
    }

    public void removeUser(String user){
        userMap.remove(user);
    }
}
