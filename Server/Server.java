package Server;

import Logic.SystemManagement;
import Logic.XmlManagement;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    private int port;
    private boolean serverAlive = true;
    private SystemManagement systemManagement;
    private XmlManagement xmlManagement;

    public Server(){
        this.port = 1888; // TODO CHANGE TO DEFAULY 1989
        this.serverAlive = true;
        systemManagement = new SystemManagement();
        this.xmlManagement = new XmlManagement(systemManagement);

        try {
            systemManagement = xmlManagement.importSystemManagementDetails() ;
            this.xmlManagement = new XmlManagement(systemManagement);
        } catch (Exception e) {//in case there is no state that is save
            e.getStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
       Server server = new Server();
       server.checkValidPort(args);
       server.runServer();
    }

    private void runServer() throws IOException {
        System.out.println("sever is running hall yea"); // TODO
        ServerSocket serverSocket = new ServerSocket(port);
        runServerSocket(serverSocket);
    }

    private void runServerSocket(ServerSocket serverSocket) throws IOException {
        while (serverAlive) {
            Socket socket = serverSocket.accept();
            socket.setKeepAlive(true);
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
                                systemManagement.saveStateToXml();
                            }
                        }
                        socket.close();
                    }
                    catch (SocketException e){
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
//           System.out.println("in executeRequest"); TODO
           Class [] classes = new Class[request.getParams().length];

           for (int i= 0; i<request.getParams().length; i++) {    // create types array
               if(request.getParams()[i].getClass() == Integer.class)
                   classes[i] = int.class;
               else if(request.getParams()[i].getClass() == Boolean.class)
                    classes[i] = boolean.class;
               else
                   classes[i] = request.getParams()[i].getClass();
           }

           Method method = systemManagement.getClass().getMethod(request.getMethod(), classes);
           if(method != null){
               System.out.println("found method "+ method.getName());// TODO
               returnValue = method.invoke(systemManagement, request.getParams());
           }
           else {
               System.out.println("method " + request.getMethod() + "not found!");// TODO
           }

       }
       catch (InvocationTargetException e){ // TODO
           System.out.println("Target execption;");
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
            System.out.println(object + "in write response"); // TODO
            outputStream.writeObject(serverResponse);
            outputStream.reset();
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

    private void setPort(String portInput){
        try {
            int portAsInt= Integer.parseInt(portInput);
            this.port = portAsInt;
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

    private void checkValidPort(String[] args) {
        String[] newArgs;
        if ( args == null || args.length == 0)
            return;

        else {
            for (int i = 0; i < args.length; i++){
                if (args[i].contains("--port=")) {
                    newArgs = args[i].split("=");
                    if (newArgs.length != 2)
                        continue;
                setPort(newArgs[1]);
                }
                else if (args[i].contains("--host=")){
                    newArgs = args[i].split("=");
                    if (newArgs.length != 2)
                        continue;
                }
            }
        }
    }
}
