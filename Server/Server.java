package Server;

import Logic.SystemManagement;
import com.sun.corba.se.impl.orbutil.ObjectWriter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    private int port = 2011;
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

                    Object obj,returnValue ;
                    ServerRequest request;
                    try{    //exception 2
                        while((obj = in.readObject()) != null) {
                            if ((request = convertObjectToRequest(obj)) != null) {
                                returnValue =  executeRequest(request);
                                writeResponseToOutPutStream(out,request,returnValue);
                            }
                        }
                        socket.close();
                    }
                    catch (ClassNotFoundException e) {  //exception 2
                        e.printStackTrace();
                    }
                    catch (IOException e) {  //exception 2
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        e.getStackTrace();
                    }
                }
                catch (IOException e) { //exception 1
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private Object executeRequest(ServerRequest request){
        Object returnValue = null;
       try {
           System.out.println("in executeRequest");
           Class [] classes = new Class[request.getParams().length];

           for (int i= 0; i<request.getParams().length; i++)    // create types array
               classes[i] = request.getParams()[i].getClass();

           Method method = systemManagement.getClass().getMethod(request.getMethod(),classes);
           if(method != null){
               System.out.println("found method "+ method.getName());
           }

           returnValue = method.invoke(systemManagement, request.getParams());
           if(returnValue == null){
               System.out.println("failed inovke!!");
           }
       }
       catch (NoSuchMethodException e) {
            e.getStackTrace();
       } catch (Exception e) {
            e.getStackTrace();
       }
       return returnValue;
    }

    private void writeResponseToOutPutStream(ObjectOutputStream outputStream,ServerRequest request,Object object){
        ServerResponse serverResponse = new ServerResponse(request,true, object);
        try {
            outputStream.reset();
            outputStream.writeObject(serverResponse);
        } catch (IOException e) {
            e.printStackTrace();
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
