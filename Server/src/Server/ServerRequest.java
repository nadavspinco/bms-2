package Server;

import java.io.Serializable;
import java.util.List;

public class ServerRequest implements Serializable {
    private String method;
    private Object[] params = null;

    public ServerRequest(String method, Object... params){
        this.method= method;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getParams() {
        return params;
    }
}
