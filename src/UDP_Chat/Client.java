package UDP_Chat;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {
    static final int port = 3241;
    static byte[] buffer = new byte[256];
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        DatagramSocket socket = new DatagramSocket();
        while (true) {
            String message = scanner.nextLine();
            buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
            socket.send(packet);
        }
    }
}
