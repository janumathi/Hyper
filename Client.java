// Java implementation for multithreaded chat client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Vector;

public class Client {
    final static int ServerPort = 1234;
    String name;

    Client(String name){
        this.name = name;
    }


    public static void main(String[] args) throws UnknownHostException, IOException
    {
        Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort);
        String nickname;
        System.out.println("Enter your username:");
        nickname = scn.nextLine();


        // obtaining input and out streams
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        ObjectInputStream oos = new ObjectInputStream(dis);
        dos.writeUTF(nickname);
        dos.flush();
        try {
            Vector<String> active =(Vector) oos.readObject();
            System.out.println(active.toString());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException c)
        {
            c.printStackTrace();
        }
        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {

                    // read the message to deliver.
                    String msg = scn.nextLine();

                    try {
                        // write on the output stream
                        dos.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}
