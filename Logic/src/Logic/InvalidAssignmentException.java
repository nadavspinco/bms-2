package Logic;

public class InvalidAssignmentException  extends Exception {
    public InvalidAssignmentException() { super(); }
    public InvalidAssignmentException(String message,Exception e) { super(message,e);}
}
