package noteApp.controller;


import noteApp.model.note.Note;
import noteApp.model.savestate.SaveState;
import noteApp.view.MainLayoutController;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.List;

/**
 * Interface for the Controller portion of the MVC architecture. Methods that somehow modify the model are defined here
 * and used from the View.
 *
 * @author Matias Vainio
 */
public interface Controller {

    /**
     * Used to check user given credentials. If they match those in the database, user is logged in.
     *
     * @param username username given by the user.
     * @param password password given by the user.
     * @return true if credentials are correct. False otherwise.
     */
    boolean logIn(String username, String password);

    /**
     * Removes user information from memory and disables everything that depends on user being logged in.
     *
     * @return true if data is successfully removed.
     */
    boolean logOut();

    /**
     * Used to create new user with user provided credentials.
     *
     * @param username username provided by the user.
     * @param password password provided by the user.
     * @return true if the creation is successful.
     * @throws Exception if signup fails
     */
    boolean signUp(String username, String password) throws Exception;

    String lzw_compress(String input);

    String lzw_extract(String input);

    /**
     * Retrieves a list of notes from the database. The list is passed as return value to the caller.
     *
     * @return list retrieved from the database.
     * @throws Exception if getting the list fails
     */
    List<Note> getList() throws Exception;

    /**
     * Creates a new note and saves it to the database.
     *
     * @param title   title of the note.
     * @param content content of the note.
     * @return Note note which has been saved to db.
     * @throws Exception if saving fails.
     */
    Note saveToDatabase(String title, String content) throws Exception;

    /**
     * Updates selected note in the database. New notes are created as empty notes which are then modified and
     * updated using this method.
     *
     * @param note note to be updated.
     * @throws Exception if updating fails
     */
    void updateNote(Note note) throws Exception;

    /**
     * Removes selected note from the database.
     *
     * @param note note to be removed.
     * @throws Exception if deleting fails
     */
    void deleteFromDatabase(Note note) throws Exception;

    /**
     * Returns selected Note
     * @return selected Noe
     */
    Note getSelectedNote();

    /**
     * Sets selected Note
     * @param note Note to set
     */
    void setSelectedNote(Note note);

    /**
     * Starts a new thread which handles the auto save feature.
     *
     * @see noteApp.model.note.AutoSaveThread for more details.
     */
    void startAutoSaveThread();

    /**
     * Moves selected note to a different (archive) collection in the database.
     *
     * @param note note to be moved
     * @see noteApp.model.note.Archiver
     */
    void archiveNote(Note note);

    /**
     * Moves selected note from archive collection to the main collection in the database.
     *
     * @param note note to be moved.
     */
    void unarchiveNote(Note note);

    /**
     * Check if user currently logged in.
     *
     * @return true if user is logged in.
     */
    boolean isLoggedIn();

    /**
     * Enables or disables properties in the view. Properties are different ui elements such as buttons and text areas.
     */
    void toggleProps();

    /**
     * Returns reference to MainLayoutController instance used in the application.
     *
     * @return MainLayoutController to be returned.
     */
    MainLayoutController getMlc();

    /**
     * Opens a rich text file onto the text area
     *
     * @param area text area to where the file is loaded
     */
    void decodeFile(InlineCssTextArea area);

    /**
     * Saves the note as a rich text file
     *
     * @param area         text area from where the note is taken
     * @param saveableNote to be saved
     */
    void encodeFile(InlineCssTextArea area, Note saveableNote);

    /**
     * Sets notes to view from offline directory.
     */
    void setOfflineNotes();

    /**
     * Adds provided note to a list in the view.
     *
     * @param note {Note} note to be added.
     */
    void addToNotesList(Note note);

    /**
     * Returns currently set save state.
     *
     * @return SaveState state to be returned.
     */
    SaveState getState();

    /**
     * Sets save state. Used to change saving functionalities between local and database.
     *
     * @param state {@link SaveState} state to be set.
     */
    void setState(SaveState state);

    /**
     * Sets notes that have the '%a' string in the title to the view.
     */
    void setArchivedNotesToView();

    /**
     * Sets notes that does not have the '%a' string in the title to the view.
     */
    void setDefaultNotesToView();

    /**
     * Sets empty list to the view.
     */
    void setEmptyList();
}
