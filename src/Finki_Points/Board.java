package Finki_Points;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Board {
    static final int ServerPort = 3421;

    public static void main(String[] args) {
        try {
            InetAddress ip_address = InetAddress.getLocalHost();
            Socket socket = new Socket(ip_address, ServerPort);

            System.out.println("Starting thread for board...");

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeUTF("Board");
            objectOutputStream.flush();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            Thread t = new Thread(new BoardWorker(socket, objectInputStream, objectOutputStream));
            t.start();
            System.out.println("Thread started...");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class BoardWorker extends Thread {
    public Socket socket;
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;

    public BoardWorker(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void run() {
        try{
            Scanner scn = new Scanner(System.in);
            while(true){
                var input = scn.nextLine();
                if(input.equals("stop")){
                    this.socket.close();
                    this.stop();
                    break;
                } else if (input.contains("GET")) {
                    objectOutputStream.writeUTF(input.split(" ")[1]);
                    objectOutputStream.flush();
                    int size = Integer.parseInt(objectInputStream.readObject().toString());
                    for (int i = 0; i < size; i++) {
                        Packet p = (Packet) objectInputStream.readObject();
                        System.out.println(p);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}