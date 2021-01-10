package UI;

public class ConnectionLostException extends RuntimeException{
    public ConnectionLostException(String message){
        super(message);
    }
    public ConnectionLostException(String message, Throwable throwable){
        super(message,throwable);
    }
}
