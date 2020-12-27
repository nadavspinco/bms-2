package Logic;

public class InvalidRegistrationException extends Exception {
    public InvalidRegistrationException()
    {
        super();
    }

    public InvalidRegistrationException(String message,Exception e)
    {
        super(message,e);
    }

}
