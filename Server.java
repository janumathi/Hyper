
import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Server
{

    public static Vector<ClientHandler> ar = new Vector<>();
    public static Vector<String> users = new Vector<>();

    static int i = 0;

    public static void main(String[] args) throws IOException
    {
        ServerSocket ss = new ServerSocket(1234);
        String name;
        Socket s;
        while (true)
        {
            s = ss.accept();

            System.out.println("New client request received : " + s);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            ObjectOutputStream ois = new ObjectOutputStream(dos);

            System.out.println("Creating a new handler for this client...");

            name = dis.readUTF();
            ClientHandler mtch = new ClientHandler(s,name, dis, dos);
            Thread t = new Thread(mtch);

            System.out.println("Adding this client to active client list");
            ar.add(mtch);
            users.add(name);
            try{
                ois.writeObject(users);
                ois.flush();
            }
            catch (IOException e){
                System.out.println(e);
            }

            t.start();
            i++;

        }
    }
}

// ClientHandler class
class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try
            {
                // receive the string
                received = dis.readUTF();

                System.out.println(received);

                if(received.equals("logout")){
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }

                // break the string into message and recipient part
                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler mc : Server.ar)
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(recipient) && mc.isloggedin==true)
                    {
                        mc.dos.writeUTF(this.name+" : "+MsgToSend);
                        break;
                    }
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
