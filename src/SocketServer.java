import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer {
    ServerSocket server;
    Socket sk;
    InetAddress addr;

    ArrayList<ServerThread> list = new ArrayList<ServerThread>();

    public SocketServer() {
        try {
            addr = InetAddress.getByName("127.0.0.1");

            server = new ServerSocket(1234, 50, addr);
            System.out.println("\n Waiting for Client connection");
            SocketClient.main(null);
            while (true) {
                sk = server.accept();
                System.out.println(sk.getInetAddress() + " connect");

                //Thread connected clients to ArrayList
                ServerThread st = new ServerThread(this);
                addThread(st);
                st.start();
            }
        } catch (IOException e) {
            System.out.println(e + "-> ServerSocket failed");
        }
    }

    public void addThread(ServerThread st) {
        list.add(st);
    }

    public void removeThread(ServerThread st) {
        list.remove(st); //remove
    }
     public void removeAllThreads(){
        list.forEach(serverThread -> list.remove(serverThread));
    }

    public void broadCast(String message) {
        for (ServerThread st : list) {
            st.pw.println(message);
        }
    }


    public static void main(String[] args) {
        new SocketServer();
    }
}


class ServerThread extends Thread {
    SocketServer server;
    PrintWriter pw;
    public String name;
    public static String firstPlayer = "";
    public static String secondPlayer = "";

    public ServerThread(SocketServer server) {
        this.server = server;
    }

    public void ReadData(String name) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(server.sk.getInputStream()));
        server.broadCast("[" + name + "] " + "has entered");

        String data;
        while ((data = br.readLine()) != null) {
            if (data == "/list") {
                pw.println("a");
            }
            server.broadCast("[" + name + "] " + data);
        }
    }

    @Override
    public void run() {
        try {
            // read
            BufferedReader br = new BufferedReader(new InputStreamReader(server.sk.getInputStream()));

            // writing
            pw = new PrintWriter(server.sk.getOutputStream(), true);

            name = br.readLine();
            try {
                FileWriter writer = new FileWriter("order.txt", true);
                writer.write(name);
                writer.write("/");
                writer.close();

            } catch (IOException e) {
                System.out.println(e);
            }
                ReadData(name);
        } catch (Exception e) {
            //Remove the current thread from the ArrayList.
            server.removeThread(this);
            server.broadCast("*[" + name + "] Left*");
            System.out.println(server.sk.getInetAddress() + " - [" + name + "] Exit");
            System.out.println(e + "---->");
        }
    }
}

