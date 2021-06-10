package noteApp.model.savestate;

import noteApp.model.Filehandler;
import noteApp.view.MainLayoutController;

/**
 * Interface for different state objects in the application. State's required functionalities are defined here.
 *
 * @author Matias Vainio
 */
public interface SaveState {

    /**
     * Creates a new note which has specified title and content.
     * @param title title to be added to the note.
     * @param content content to be added to the note.
     */
    void createNote(String title, String content);

    /**
     * Updates the view with updated list of notes.
     */
    void setList();


    /**
     * Sets archived notes to the list in view.
     */
    void setArchiveList();

    /**
     * Updates note which is then saved based on the state which is currently in use.
     */
    void handleNote();

    /**
     * Removes a note from list in view. If application is in online state the note is removed
     * from the database also.
     */
    void handleDelete();

    /**
     * Retrieves notes which are saved on the local machine.
     * @param filehandler reference to {@link Filehandler} used to read the files.
     * @param mlc reference to the {@link MainLayoutController}.
     */
    void getNotesFromDisk(Filehandler filehandler, MainLayoutController mlc);
}
