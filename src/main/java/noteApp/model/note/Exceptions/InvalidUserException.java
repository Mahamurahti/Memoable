package noteApp.model.note.Exceptions;

/**
 * Thrown to indicate attempt to use invalid user credentials.
 */
public class InvalidUserException extends Exception {
    public InvalidUserException(String title) { super("Invalid user: " + title); }
}
