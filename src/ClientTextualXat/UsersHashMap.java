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
        for(String user : userMap.keySet()){
            userMap.get(user).write("[n]" + nick);
        }
        String userlist = "[u]" + nick + ",";
        for(String user : userMap.keySet()){
            if(!user.equals(nick))
                userlist += user + ",";
        }
        userMap.get(nick).write(userlist.substring(0, userlist.length()-1)); //enviem la llista d'usuaris

        return nick;
    }

    public void sendMessage(String sender, String message){

      int senderColor = 31 + (sender.toLowerCase().charAt(0) - 'a') % 6;
        for(String user : userMap.keySet()){
            if(!user.equals(sender))
                userMap.get(user).write("[m]\033["+ senderColor +"m<" + sender + ">\033[0m" + message);
        }
    }

    public void send(String reciever, String message){
        userMap.get(reciever).write(message);
    }

    public void removeUser(String nick){
        userMap.remove(nick);
        for(String user : userMap.keySet()){
          userMap.get(user).write("[d]"+nick);
        }
    }
}
