package UDP_Chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {

    static final int port = 3241;

    public static void main(String[] args) throws SocketException {

        while (true) {

            DatagramSocket datagramSocket = new DatagramSocket(port);
            byte[] buffer = new byte[256];

             try {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                InetAddress address = datagramPacket.getAddress();
                int p = datagramPacket.getPort();
                String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                datagramPacket = new DatagramPacket(buffer, buffer.length, address, p);
                datagramSocket.send(datagramPacket);
            } catch (IOException e) {
                 break;
             }
        }
    }
}
