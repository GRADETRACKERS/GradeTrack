/** Our own exception. We throw it whenever the user types something wrong. */
public class GradeException extends Exception {
    public GradeException(String message) { super(message); }   // the message is shown in an error box
}
