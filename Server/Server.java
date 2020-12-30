package Server;

import Logic.SystemManagement;
import com.sun.corba.se.impl.orbutil.ObjectWriter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port = 1989;
    private boolean serverAlive = true;
    private SystemManagement systemManagement = new SystemManagement();

    public static void main(String[] args) throws IOException {
       Server server = new Server();
       server.runServer(args);
    }

    private void runServer(String[] args) throws IOException {
        System.out.println("sever is running hall yea");
        setPortInput(args);
        ServerSocket serverSocket = new ServerSocket(port);
        runServerSocket(serverSocket);
    }

    private void runServerSocket(ServerSocket serverSocket) throws IOException {
        while (serverAlive) {
            Socket socket = serverSocket.accept();
            new Thread(()-> {
                System.out.println("helo socket");
                try(ObjectInputStream in = new ObjectInputStream(socket.getInputStream());  //exception 1
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                    Object obj, obj1 = new Object();
                    ServerRequest request;
                    try{    //exception 2
                        while((obj = in.readObject()) != null) {
                            if ((request = convertObjectToRequest(obj)) != null) {
                                executeRequest(request);
                                out.writeObject(obj1);
                            }
                        }
                    }
                    catch (ClassNotFoundException e) {  //exception 2
                        e.printStackTrace();
                    }
                    catch (IOException e) {  //exception 2
                        e.printStackTrace();
                    }
                }
                catch (IOException e) { //exception 1
                    e.printStackTrace();
                }
            }).run();
        }

    }
    private void executeRequest(ServerRequest request){
       try {
           Method method = systemManagement.getClass().getDeclaredMethod(request.getMethod());
           method.invoke(request.getParams());
       }
       catch (NoSuchMethodException e) {
            e.getStackTrace();
       } catch (Exception e) {
            e.getStackTrace();
       }
    }

    private ServerRequest convertObjectToRequest(Object obj) {
        ServerRequest serverRequest = null;
        if(obj instanceof ServerRequest){
            serverRequest = (ServerRequest)obj;
        }
        return serverRequest;
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
