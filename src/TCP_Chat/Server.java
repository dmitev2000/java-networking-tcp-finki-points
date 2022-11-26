package TCP_Chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class Server {
    static final int port = 3434;
    static List<User> users = new ArrayList<>();
    static Vector<ServerWorker> threads = new Vector<>();

    static public void logOutUser(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).username.equals(username)) {
                users.remove(i);
                break;
            }
        }
    }

    static public int getIndex(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).username.equals(username)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.flush();
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                System.out.println("New user in queue...");

                ServerWorker worker = new ServerWorker(socket, objectInputStream, objectOutputStream);
                threads.add(worker);

                Thread thread = new Thread(worker);
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class ServerWorker extends Thread {
    Socket socket;
    User user;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;

    public ServerWorker(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void run() {
        // Login
        try {
            objectOutputStream.writeUTF("Enter username: ");
            objectOutputStream.flush();
            User user = (User) objectInputStream.readObject();
            this.user = user;
            Server.users.add(user);
            System.out.println(this.user + " is now online.");
            objectOutputStream.writeUTF("You are now logged in, " + user.username);
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Communicating with logged user
        while (true) {
            try {
                String input = objectInputStream.readUTF();
                // Send list of online users
                if (input.equals("users")) {
                    objectOutputStream.writeUTF(String.valueOf(Server.users.size()));
                    objectOutputStream.flush();
                    for (int i = 0; i < Server.users.size(); i++) {
                        objectOutputStream.writeObject(Server.users.get(i));
                        objectOutputStream.flush();
                    }
                }
                // Forward message to user
                else if (input.equals("send")) {
                    Message message = (Message) objectInputStream.readObject();
                    System.out.println(message.from + " sends message to " + message.to);
                    System.out.println(message);
                    for (int i = 0; i < Server.threads.size(); i++) {
                        if (Objects.equals(Server.threads.get(i).user.username, message.to)) {
                            Server.threads.get(i).objectOutputStream.writeObject(message);
                            Server.threads.get(i).objectOutputStream.flush();
                            break;
                        }
                    }
                    objectOutputStream.writeUTF("Message sent!");
                    objectOutputStream.flush();
                }
                // Bad input handler
                else {
                    objectOutputStream.writeUTF("The server doesn't understand this command! (" + input + ")");
                    objectOutputStream.flush();
                }
            } catch (IOException e) {
                // Log out
                int index = Server.getIndex(this.user.username);
                Server.logOutUser(this.user.username);
                Server.threads.remove(index);
                System.out.println(this.user + " is now offline.");
                break;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
