package Logic;
public class EmailAlreadyExistException extends Exception
{
    public EmailAlreadyExistException(String Message,Exception  exception)
    {
        super(Message,exception);
    }
    public EmailAlreadyExistException (String Message){
        super(Message);
    }
}
