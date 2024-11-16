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
        for(String user : userMap.keySet()){
            if(!user.equals(sender))
                userMap.get(user).write("<" + sender + ">" + message);
        }
    }

    public void removeUser(String user){
        userMap.remove(user);
    }
}
