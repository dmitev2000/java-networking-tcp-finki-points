package Finki_Points;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Teacher {
    static final int ServerPort = 3421;
    public static void main(String[] args) {
            try {
                InetAddress ip_address = InetAddress.getLocalHost();
                Socket socket = new Socket(ip_address, ServerPort);

                /*
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                Thread t = new Thread(new TeacherWorker(socket, dataInputStream, dataOutputStream));
                */

                System.out.println("Starting thread for teacher...");

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeUTF("Teacher");
                objectOutputStream.flush();
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Thread t = new Thread(new TeacherWorker(socket, objectInputStream, objectOutputStream));

                t.start();

                System.out.println("Thread started...");

            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}

class TeacherWorker extends Thread {
    public Socket socket;
    public DataInputStream dataInputStream;
    public DataOutputStream dataOutputStream;
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;

    public TeacherWorker(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    public TeacherWorker(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
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
                }
                var arr = input.split(" ");
                Packet packet = new Packet(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Double.parseDouble(arr[3]));
                //System.out.println(packet.courseId + " " + packet.index + " " + packet.acitvityId + " "  + packet.points);
                objectOutputStream.writeObject(packet);
                //this.dataOutputStream.writeUTF(input);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}