package TCP_Chat;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    static final int port = 3434;

    public static void main(String[] args) {
        try {
            InetAddress ip_address = InetAddress.getLocalHost();
            Socket socket = new Socket(ip_address, port);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.flush();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            Thread t = new Thread(new ClientWorker(socket, objectInputStream, objectOutputStream));
            t.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class ClientWorker extends Thread {
    public Socket socket;
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;

    public User user;

    public ClientWorker(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        // Login
        try {
            System.out.println(objectInputStream.readUTF());
            String username = scanner.nextLine();
            this.user = new User(username, socket.getLocalAddress());
            objectOutputStream.writeObject(this.user);
            objectOutputStream.flush();
            System.out.println(objectInputStream.readUTF());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Logged in
        while (true) {
                try {
                    String input = scanner.nextLine();
                    // Log out
                    if (input.equals("logout")) {
                        socket.close();
                        this.stop();
                        break;
                    }
                    // Get list of all online users
                    else if (input.equals("users")) {
                        objectOutputStream.writeUTF(input);
                        objectOutputStream.flush();
                        int size = Integer.parseInt(objectInputStream.readUTF());
                        for (int i = 0; i < size; i++) {
                            User u = (User) objectInputStream.readObject();
                            System.out.println(u);
                        }
                    // Send message to another user
                    } else if (input.equals("send")) {
                        objectOutputStream.writeUTF(input);
                        objectOutputStream.flush();
                        System.out.println("Send message to (username):");
                        String to = scanner.nextLine();
                        System.out.println("Message content (body):");
                        String content = scanner.nextLine();
                        objectOutputStream.writeObject(new Message(this.user.username, to, content));
                        objectOutputStream.flush();
                        System.out.println(objectInputStream.readUTF());
                    }
                    // Bad input handler
                    else {
                        objectOutputStream.writeUTF(input);
                        objectOutputStream.flush();
                        System.out.println(objectInputStream.readUTF());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
        }
    }
}
