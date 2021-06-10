package noteApp.model.note;

import noteApp.model.backend.NoteConnection;
import noteApp.model.note.Exceptions.InvalidNoteException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import java.io.IOException;
import java.util.List;


/**
 * Note Data Access Object. Used when accessing note data in database.
 *
 * @author Matias Vainio
 */
public class NoteDAOImpl implements NoteDAO {

    @Override
    public List<Note> getAll() throws IOException, InterruptedException {
        Session session = SecurityUtils.getSubject().getSession();

        return NoteConnection.getInstance().getUserNotes(
                session.getAttribute("id").toString(),
                session.getAttribute("token").toString()
        );
    }

    /**
     * Inserts new note to the database.
     *
     * @param note note to be inserted.
     */
    @Override
    public Note create(Note note) throws InvalidNoteException, IOException, InterruptedException {
        Session session = SecurityUtils.getSubject().getSession();
        if (note == null)
            throw new NullPointerException();
        if (note.getTitle() == null || note.getContent() == null) 
            throw new InvalidNoteException("Tried to create an empty note");
        return NoteConnection.getInstance().saveNote(note,
                session.getAttribute("id").toString(),
                session.getAttribute("token").toString()
        );
    }

    /**
     * Updates selected note in database.
     *
     * @param note note to be updated.
     */
    @Override
    public void update(Note note) throws InvalidNoteException, IOException, InterruptedException {
        if (note.getTitle() == null && note.getContent() == null) {
            throw new InvalidNoteException("Trying to modify unknown note.");
        }
        NoteConnection.getInstance().saveNote(note,
                SecurityUtils.getSubject().getSession().getAttribute("id").toString(),
                SecurityUtils.getSubject().getSession().getAttribute("token").toString()
        );
    }

    /**
     * Removes provided note from the database.
     *
     * @param note {Note} note to be removed.
     */
    @Override
    public void delete(Note note) throws IOException, InterruptedException {
        NoteConnection.getInstance().deleteNote(
                note.getId().toString(),
                SecurityUtils.getSubject().getSession().getAttribute("id").toString(),
                SecurityUtils.getSubject().getSession().getAttribute("token").toString()
        );
    }
}
