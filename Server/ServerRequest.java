package Server;

import java.io.Serializable;
import java.util.List;

public class ServerRequest implements Serializable {
    private String method;
    private Object [] params;

    public ServerRequest(String method, Object... params){
        this.method= method;
        this.params = params;
    }

}
