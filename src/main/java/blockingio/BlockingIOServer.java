package blockingio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingIOServer {

    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(SERVER_PORT,20);

        System.out.println("Start blockingio.server ");

        while (true) {
            Socket client = server.accept(); // system call accept;
            System.out.println("Client connection coming in with port : " + client.getPort());

            new Thread(() -> {
                InputStream in = null;
                try {
                    in = client.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    while(true){
                        String dataline = reader.readLine();

                        if(null != dataline) {
                            System.out.println(dataline);
                        }else{
                            client.close();
                            break;
                        }
                    }
                    System.out.println("Client connection is closed");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
