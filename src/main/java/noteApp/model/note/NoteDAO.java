package noteApp.model.note;

import noteApp.model.note.Exceptions.InvalidNoteException;

import java.io.IOException;
import java.util.List;

/**
 * Data Access Object interface for Note objects. Defines functionality which are needed for Note CRUD operations.
 * @author Matias Vainio
 */
public interface NoteDAO {

    /**
     * Retrieves all notes for the current user from the database.
     * @return list of note objects.
     * @throws IOException if connection url is incorrect
     * @throws InterruptedException if connection is stopped
     */
    List<Note> getAll() throws IOException, InterruptedException;

    /**
     * Creates a Note object which is passed to the database.
     * @param note {Note} note object to be passed.
     * @throws InvalidNoteException exception is thrown if the note is not valid.
     * @return Note returns note data when creation is successful.
     * @throws IOException if connection url is incorrect
     * @throws InterruptedException if connection is stopped
     */
    Note create(Note note) throws InvalidNoteException, IOException, InterruptedException;

    /**
     * Updates a Note in database by updating fields with new value.
     * @param note Note object that holds new values to be updated.
     * @throws InvalidNoteException exception is thrown if the note is not valid.
     * @throws IOException if connection url is incorrect
     * @throws InterruptedException if connection is stopped
     */
    void update(Note note) throws InvalidNoteException, IOException, InterruptedException;

    /**
     * Removes provided note.
     * @param note {Note} note to be removed
     * @throws IOException if connection url is incorrect
     * @throws InterruptedException if connection is stopped
     */
    void delete(Note note) throws IOException, InterruptedException;

}
