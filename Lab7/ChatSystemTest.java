package Lab7;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class NoSuchRoomException extends Exception
{
    public NoSuchRoomException(String message)
    {
        super(message);
    }
}

class NoSuchUserException extends Exception
{
    public NoSuchUserException(String message)
    {
        super(message);
    }
}
class ChatRoom
{
    String name;
    Set<String> users;

    public ChatRoom(String name) {
        this.name = name;
        this.users = new TreeSet<>();
    }

    public void addUser(String username) {
        users.add(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public boolean hasUser(String username) {
        return users.contains(username);
    }
    public int numUsers()
    {
        return users.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("\n");
        if (users.isEmpty())
        {
            sb.append("EMPTY\n");
        }
        else {
            for (String user: users)
            {
                sb.append(user + "\n");
            }
        }
        return sb.toString();
    }
}

class ChatSystem {
    TreeMap<String, ChatRoom> chatRooms;
    Set<String> users;

    public ChatSystem() {
        this.chatRooms = new TreeMap<>();
        this.users = new TreeSet<>();
    }

    public void addRoom(String roomName) {
        chatRooms.put(roomName, new ChatRoom(roomName));
    }

    public void removeRoom(String roomName) {
        chatRooms.remove(roomName);
    }

    public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        if (!chatRooms.containsKey(roomName)) {
            throw new NoSuchRoomException(roomName);
        }
        return chatRooms.get(roomName);
    }

    public ChatRoom findMinUsersRoom() {
        if (chatRooms.isEmpty())
            return null;
        ChatRoom min = chatRooms.get(chatRooms.firstKey());
        for (String roomName : chatRooms.keySet()) {
            ChatRoom current = chatRooms.get(roomName);
            if (current.numUsers() < min.numUsers())
                min = current;
        }
        return min;
    }

    public void register(String userName) throws NoSuchRoomException, NoSuchUserException {
        users.add(userName);
        ChatRoom min = findMinUsersRoom();
        if (min == null)
            return;
        joinRoom(userName, min.name);
    }

    public void checkIfExist(String userName, String roomName) throws NoSuchRoomException, NoSuchUserException {
        if (!chatRooms.containsKey(roomName))
            throw new NoSuchRoomException(roomName);
        if (!users.contains(userName))
            throw new NoSuchUserException(userName);
    }

    public void joinRoom(String userName, String roomName) throws NoSuchRoomException, NoSuchUserException {
        checkIfExist(userName, roomName);
        chatRooms.get(roomName).addUser(userName);
    }

    public void registerAndJoin(String userName, String roomName) throws NoSuchRoomException, NoSuchUserException {
        //register(userName);
        users.add(userName);
        joinRoom(userName, roomName);
    }

    public void leaveRoom(String userName, String roomName) throws NoSuchRoomException, NoSuchUserException {
        checkIfExist(userName, roomName);
        chatRooms.get(roomName).removeUser(userName);
    }

    public void followFriend(String userName, String friend_username) throws NoSuchUserException {
        if (!users.contains(userName))
            throw new NoSuchUserException(userName);

        for (ChatRoom room: chatRooms.values())
        {
            if (room.hasUser(friend_username))
            {
                room.addUser(userName);
            }
        }
    }
}

public class ChatSystemTest {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException, InvocationTargetException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr.addUser(jin.next());
                if ( k == 1 ) cr.removeUser(jin.next());
                if ( k == 2 ) System.out.println(cr.hasUser(jin.next()));
            }
            System.out.println("");
            System.out.println(cr.toString());
            n = jin.nextInt();
            if ( n == 0 ) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr2.addUser(jin.next());
                if ( k == 1 ) cr2.removeUser(jin.next());
                if ( k == 2 ) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }
        if ( k == 1 ) {
            ChatSystem cs = new ChatSystem();
            Method mts[] = cs.getClass().getMethods();
            while ( true ) {
                String cmd = jin.next();
                if ( cmd.equals("stop") ) break;
                if ( cmd.equals("print") ) {
                    System.out.println(cs.getRoom(jin.next())+"\n");continue;
                }
                for ( Method m : mts ) {
                    if ( m.getName().equals(cmd) ) {
                        String params[] = new String[m.getParameterTypes().length];
                        for ( int i = 0 ; i < params.length ; ++i ) params[i] = jin.next();
                        m.invoke(cs,params);
                    }
                }
            }
        }
    }

}


