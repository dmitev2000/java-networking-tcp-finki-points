package Finki_Points;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static final int port = 3421;
    public static ArrayList<Packet> data;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            data = new ArrayList<>();
            data.add(new Packet(1,192007,2,100.0));
            data.add(new Packet(1,192011,2,70.0));
            data.add(new Packet(2,203017,2,44.5));

            while (true) {
                Socket conn = serverSocket.accept();

                /*
                DataInputStream dataInputStream = new DataInputStream(conn.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                ServerWorker worker = new ServerWorker(conn, dataInputStream, dataOutputStream);
                */

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(conn.getOutputStream());
                objectOutputStream.flush();
                ObjectInputStream objectInputStream = new ObjectInputStream(conn.getInputStream());
                String userType = objectInputStream.readUTF();

                if (userType.equals("Teacher")) {
                    System.out.println("New request from Teacher...");
                    ServerWorker worker1 = new ServerWorker(conn, objectInputStream, objectOutputStream);
                    System.out.println("Creating new thread handler for " + userType + "...");
                    Thread t = new Thread(worker1);
                    t.start();
                } else {
                    System.out.println("New request from Board...");
                    ServerWorkerForBoard worker2 = new ServerWorkerForBoard(conn, objectInputStream, objectOutputStream);
                    System.out.println("Creating new thread handler for " + userType + "...");
                    Thread t = new Thread(worker2);
                    t.start();
                }
        }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

class ServerWorkerForBoard extends Thread {
    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    ServerWorkerForBoard(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void run() {
        String received;
        int courseId = -1;
        while (true) {
            try {
                received = objectInputStream.readUTF();
                courseId = Integer.parseInt(received.toString());
                if (courseId == -1) {
                    objectInputStream.close();
                    objectOutputStream.close();
                    objectOutputStream.flush();
                    socket.close();
                }else {
                    ArrayList<Packet> packets = new ArrayList<>();
                    for(int i = 0; i < Server.data.size(); i++){
                        if(Server.data.get(i).courseId == courseId){
                            System.out.println(Server.data.get(i));
                            packets.add(new Packet(Server.data.get(i)));
                        }
                    }
                    objectOutputStream.writeObject(packets.size());
                    objectOutputStream.flush();
                    for (Packet packet : packets) {
                        objectOutputStream.writeObject(new Packet(packet));
                        objectOutputStream.flush();
                    }
                }
            } catch (IOException e) {
                System.out.println("Proccess " + socket + " stopped.");
                break;
            }
        }
    }
}

class ServerWorker extends Thread{
    Socket s;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    ServerWorker(Socket s, DataInputStream dataInputStream, DataOutputStream dataOutputStream){
        this.s = s;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    ServerWorker(Socket s, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        this.s = s;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void run(){
        //String received;
        Packet received = null;
        while(true){
            try {
                received = (Packet) objectInputStream.readObject();
                //received = dataInputStream.readUTF();
                if(received != null) {
                    Server.data.add(received);
                    System.out.println("New entry added...");
                    objectOutputStream.writeUTF("New entry added successfully.");
                    objectOutputStream.flush();
                    System.out.println(received);
                } else {
                    this.s.close();
                    this.objectOutputStream.close();
                    this.objectInputStream.close();
                    this.objectOutputStream.flush();
                    break;
                }
            }catch (EOFException eofe){
                System.out.println("Proccess " + s + " stopped.");
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Packet implements Serializable {
    public int courseId;
    public int index;
    public int acitvityId;
    public double points;

    public Packet(int c, int i, int a, double p) {
        this.courseId = c;
        this.index = i;
        this.acitvityId = a;
        this.points = p;
    }

    public Packet(Packet packet) {
        this.courseId = packet.courseId;
        this.index = packet.index;
        this.acitvityId = packet.acitvityId;
        this.points = packet.points;
    }

    @Override
    public String toString(){
        return "Entry{courseId="+ courseId +", index="+ index +", acitvityId="+ acitvityId + ", points=" + points + "}";
    }
}
