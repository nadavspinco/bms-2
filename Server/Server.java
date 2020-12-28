package Server;

import com.sun.corba.se.impl.orbutil.ObjectWriter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port = 1989;
    private boolean serverAlive = true;

    public static void main(String[] args) throws IOException {
       Server server = new Server();
       server.runServer(args);

    }

    private void runServer(String[] args) throws IOException {
        setPortInput(args);
        ServerSocket serverSocket = new ServerSocket(port);
    }

    private void runServerSocket(ServerSocket serverSocket) throws IOException {
        while (serverAlive) {
            Socket socket = serverSocket.accept();

            try (BufferedReader in =
                         new BufferedReader(
                                 new InputStreamReader(
                                         socket.getInputStream()));
                 PrintWriter out =
                         new PrintWriter(
                                 socket.getOutputStream(),
                                 true)) {
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private void setPortInput(String[] args) {
        if(args.length > 0){
            setPort(args[0]);
        }
    }

    private void setPort(String portInput){
        try {
            int portAsInt= Integer.parseInt(portInput);
            this.port = portAsInt;
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }








}
