package nonblockingio.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 */
public class client {

    public static void main(String[] args) {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket("localhost", 8888);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            //BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //sentence = inFromUser.readLine();
            outToServer.writeBytes("Hello ..." + '\n');
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String sentence = inFromServer.readLine();
            System.out.println("Response from Server : " + sentence);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}