package noteApp.model.note.Exceptions;

/**
 * Thrown to indicate attempt to use invalid note.
 */
public class InvalidNoteException extends Exception {
    public InvalidNoteException(String title) {
        super("Invalid note. " + title);
    }
}
