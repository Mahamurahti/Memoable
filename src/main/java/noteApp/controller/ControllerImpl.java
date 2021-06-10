package noteApp.controller;

import noteApp.model.Filehandler;
import noteApp.model.Lzw;
import noteApp.model.note.*;
import noteApp.model.note.Exceptions.InvalidNoteException;
import noteApp.model.savestate.SaveState;
import noteApp.model.user.UserDAO;
import noteApp.model.user.UserDAOImpl;
import noteApp.utils.AES.Crypter;
import noteApp.view.MainLayoutController;
import noteApp.view.RootLayoutController;
import org.apache.commons.lang.NullArgumentException;
import org.apache.maven.surefire.shared.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

/**
 * Represents the Controller portion of the MVC model. Used to pass data between the Model and the View.
 *
 * @author Matias Vainio
 * @author Eric Keränen
 * @author Jere Salmensaari
 * @author Teemu Viljanen
 * @author Nico Järvinen
 */
public class ControllerImpl implements Controller {
    private static AutoSaveThread autoSaveThread;
    /**
     * Salt used in hashing the user's password.
     */
    private final String salt = "reUDjgjqyQMEgevN6paM";
    private final Lzw compressor;
    private final Crypter crypter;
    private final NoteDAO noteDAO;
    private final UserDAO userDAO;
    /**
     * Archiver used in archiving and unarchiving of notes.
     */
    private final Archiver archiver;
    private Filehandler filehandler;
    private MainLayoutController mlc;
    private RootLayoutController rlc;
    /**
     * State that handles how and where the notes are saved. Also how new notes are displayed to the user.
     */
    private SaveState state;

    public ControllerImpl(RootLayoutController rlc, MainLayoutController mlc) {
        this.mlc = mlc;
        this.rlc = rlc;
        this.filehandler = new Filehandler(this);
        this.noteDAO = new NoteDAOImpl();
        this.userDAO = new UserDAOImpl(mlc, this);
        this.crypter = new Crypter();
        this.compressor = new Lzw();
        this.archiver = new Archiver(this);
        autoSaveThread = new AutoSaveThread(rlc, mlc, this);
    }

    public ControllerImpl() {
        this.userDAO = new UserDAOImpl();
        this.noteDAO = new NoteDAOImpl();
        this.crypter = new Crypter();
        this.compressor = new Lzw();
        this.archiver = new Archiver(this);
        autoSaveThread = new AutoSaveThread(rlc, mlc, this);
    }

    /**
     * Used to log user in to the program. Credentials are provided as parameters.
     *
     * @param username username provided by the user.
     * @param password password provided by the user.
     * @return true if login is successful. False otherwise.
     */
    @Override
    public boolean logIn(String username, String password) {
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("password", password);
        autoSaveThread.enable();
        boolean logIn;
        try {
            logIn = userDAO.logIn(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            logIn = false;
        }
        return logIn;
    }

    /**
     * Logs current user out of the application.
     * Also disables auto save thread and empties the saveable note.
     *
     * @return true if the log out procedure is successful.
     */
    @Override
    public boolean logOut() {
        autoSaveThread.cancel();
        if (mlc != null) {
            this.mlc.setSaveableNote(null);
        }
        return this.userDAO.logOut();
    }

    /**
     * Creates new user with provided credentials.
     *
     * @param username username provided by the user.
     * @param password password provided by the user.
     */
    @Override
    public boolean signUp(String username, String password) throws Exception {
        if (userDAO.create(username, password)) {
            this.logIn(username, password);
            return true;
        }
        return false;
    }

    /**
     * Retrieves current user's id.
     * @return id to be returned.
     */
    private String getUserId() {
        if (SecurityUtils.getSubject().getSession().getAttribute("id") == null) {
            return null;
        }
        return SecurityUtils.getSubject().getSession().getAttribute("id").toString();
    }

    /**
     * Compresses provided string with the lzw algorithm.
     * @param input string to be compressed.
     * @return compressed string.
     */
    public String lzw_compress(String input) {
        return compressor.lzw_compress(input);
    }

    /**
     * Decompresses provided string with the lzw algorithm.
     * @param input string to be decompressed.
     * @return decompressed string.
     */
    public String lzw_extract(String input) {
        return compressor.lzw_extract(input);
    }

    /**
     * Retrieves note that has been selected by the user from the view.
     * @return note that has been selected.
     */
    @Override
    public Note getSelectedNote() {
        return mlc.getSelectedNote();
    }

    /**
     * Sets selected note with the provided note in the view.
     * @param note note to be set.
     */
    @Override
    public void setSelectedNote(Note note) {
        this.mlc.setSelectedNote(note);
    }

    /**
     * Encrypts provided string with hex value generated by user's id and salt.
     * @param content String to be encrypted.
     * @return encrypted string.
     */
    private String cryptContent(String content) {
        String currentUserID = getUserId();

        String prehash = currentUserID + this.salt;

        String hashHex = DigestUtils.md5Hex(prehash);
        String crypted = this.crypter.encryptAES128(content, hexToString(hashHex));
        return compressor.lzw_compress(crypted);
    }

    /**
     * Creates a new note and saves it to the database. Content of the note is crypted before it gets transferred to
     * the database.
     *
     * @param name title of the note.
     * @param text content of the note.
     * @throws Exception exception with a message.
     */
    public Note saveToDatabase(String name, String text) throws Exception {
        String currentUserID;
        try {
            currentUserID = getUserId();
        } catch (Exception e) {
            throw new Exception("UserID missing");
        }

        String content = cryptContent(text);

        Note note = new Note();
        Note newNote;

        note.setTitle(name);
        note.setContent(content);
        note.setUserId(currentUserID);
        note.setDate(new Date());
        try {
            newNote = noteDAO.create(note);
        } catch (Exception e) {
            throw new Exception("Note saving failed");
        }
        return newNote;
    }

    /**
     * Converts hex string to a normal string.
     * @param input hex value to be converted.
     * @return String to be returned.
     */
    private String hexToString(String input) {
        String result = "";
        char[] charArray = input.toCharArray();
        for (int i = 0; i < charArray.length; i = i + 2) {
            String st = "" + charArray[i] + "" + charArray[i + 1];
            char ch = (char) Integer.parseInt(st, 16);
            result = result + ch;
        }
        return result;
    }

    /**
     * Updates provided note in the database.
     * @param note note to be updated.
     * @throws InvalidNoteException if the note's content is empty or saving to database fails.
     */
    @Override
    public void updateNote(Note note) throws InvalidNoteException {
        String content = note.getContent();
        if (content == null) {
            throw new InvalidNoteException("Note content is empty");
        }
        content = cryptContent(content);
        note.setContent(content);
        try {
            noteDAO.update(note);
        } catch (Exception e) {
            throw new InvalidNoteException("Note update failed.");
        }
    }

    /**
     * Retrieves notes from the database and adds them to a list.
     * @return list of notes.
     * @throws IOException if the HTTP request is invalid.
     * @throws InterruptedException if the request is interrupted.
     */
    @Override
    public List<Note> getList() throws IOException, InterruptedException {
        String currentUserID = getUserId();
        if (currentUserID == null) {
            return null;
        }
        String prehash = currentUserID + this.salt;
        String hashHex = DigestUtils.md5Hex(prehash);

        List<Note> notes = null;
        try {
            notes = noteDAO.getAll();
        } catch (IOException ie) {
            throw new IOException("Invalid input.");
        }
        catch (InterruptedException e) {
            throw new InterruptedException("Request was interrupted.");
        }

        assert notes != null;
        notes.forEach(n -> {
            String note = n.getContent();
            note = this.compressor.lzw_extract(note);
            note = this.crypter.decryptAES128(note, hexToString(hashHex));
            n.setContent(note);
        });

        return notes;
    }

    /**
     * Deletes provided note from the database.
     * @param note note to be removed.
     * @throws IOException if the HTTP request is invalid.
     * @throws InterruptedException if the request is interrupted.
     * @throws InvalidNoteException if the provided note is invalid.
     */
    @Override
    public void deleteFromDatabase(Note note) throws IOException, InterruptedException, InvalidNoteException {
        if (note == null) {
            throw new InvalidNoteException("Invalid note.");
        }
        try {
            noteDAO.delete(note);
        } catch (IOException ie) {
            throw new IOException("Invalid input.");
        }
        catch (InterruptedException e) {
            throw new InterruptedException("Request was interrupted.");
        }
    }

    /**
     * Starts the auto save thread.
     */
    @Override
    public void startAutoSaveThread() {
        autoSaveThread.startThread();
    }

    /**
     * Archives provided note in the view.
     * @param note note to be moved
     */
    @Override
    public void archiveNote(Note note) {
        archiver.archiveNote(note);
    }

    /**
     * Unarchives provided note in the view.
     * @param note note to be moved.
     */
    @Override
    public void unarchiveNote(Note note) {
        archiver.returnFromArchive(note);
    }

    /**
     * Check if current user is logged in.
     * @return true if user is logged in. False otherwise.
     */
    @Override
    public boolean isLoggedIn() {
        boolean isLoggedIn;
        try {
            String id = getUserId();
            isLoggedIn = id != null;
        } catch (Exception e) {
            return false;
        }
        return isLoggedIn;
    }

    /**
     * Enables view's nodes based on if the user continues in offline mode.
     */
    @Override
    public void toggleProps() {
        mlc.disableProps();
    }

    /**
     * Returns reference to the {@link MainLayoutController}.
     * @return MainLayoutController to be returned.
     */
    @Override
    public MainLayoutController getMlc() {
        return this.mlc;
    }

    /**
     * Opens a rich text file to the views text area.
     * @param note text area where the note content is opened.
     */
    @Override
    public void decodeFile(InlineCssTextArea note) {
        filehandler.decodeFile(note, null);
    }

    /**
     * Opens a rich text file onto the text area
     *
     * @param note area to where the file is loaded.
     * @param saveableNote note to be opened.
     */
    @Override
    public void encodeFile(InlineCssTextArea note, Note saveableNote) {
        filehandler.encodeFile(note, saveableNote, null);
    }

    /**
     * Sets notes to view from offline directory.
     */
    @Override
    public void setOfflineNotes() {
        getState().getNotesFromDisk(filehandler, mlc);
    }

    /**
     * Add provided note to list which is in the view portion of the app.
     *
     * @param note {Note} note to be added.
     */
    @Override
    public void addToNotesList(Note note) {
        this.mlc.getNoteList().add(note);
        this.mlc.setList();
    }

    /**
     * Returns currently set save state.
     *
     * @return SaveState to be returned.
     */
    @Override
    public SaveState getState() {
        return this.state;
    }

    /**
     * Set provided state as global save state.
     *
     * @param state {@link SaveState} state to be set.
     */
    @Override
    public void setState(SaveState state) {
        this.state = state;
    }

    /**
     * Sets archived note to the view.
     */
    @Override
    public void setArchivedNotesToView() {
        mlc.setArchivedNotesToView();
    }

    /**
     * Sets unarchived notes to the view.
     */
    @Override
    public void setDefaultNotesToView() {
        mlc.setDefaultNotesToView();
    }

    /**
     * Sets an empty list tot the view.
     */
    @Override
    public void setEmptyList() {
        mlc.setEmptyList();
    }
}
