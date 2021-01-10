package Server;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    private ServerRequest serverRequest;
    private Object returnValue;
    private boolean isSucceed ;

    public Object getReturnValue() {
        return returnValue;
    }


    public ServerRequest getServerRequest() {
        return serverRequest;
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public ServerResponse(ServerRequest serverRequest, boolean isSucceed,Object returnValue) {
        this.returnValue = returnValue;
        this.serverRequest = serverRequest;
        this.isSucceed = isSucceed;
    }
}
