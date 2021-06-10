package noteApp.model.savestate;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import noteApp.model.Context;
import noteApp.model.Filehandler;
import noteApp.model.note.Note;
import noteApp.view.MainLayoutController;
import noteApp.view.Toast;

import java.util.ResourceBundle;

/**
 * A state of the program where user doesn't have to log in. Note CRUD operations happen locally.
 *
 * @author Matias Vainio
 */
public class OfflineState extends AbstractState {
    private final MainLayoutController mlc;
    /**
     * Used to store notes to the user's computer.
     */
    private final Filehandler filehandler;

    public OfflineState(MainLayoutController mlc) {
        this.mlc = mlc;
        this.filehandler = new Filehandler();
        getNotesFromDisk(filehandler, mlc);
    }

    /**
     * Creates a new Note object and adds it to list in the view.
     *
     * @param title   title of the note.
     * @param content content of the note.
     */
    @Override
    public void createNote(String title, String content) {
        Note newNote = new Note(title, content);
        mlc.getNoteList().add(newNote);
    }

    /**
     * Sets notes to the list in view.
     */
    @Override
    public void setList() {
        if (this.mlc != null) {
            mlc.stopListening();
            mlc.setNotes(FXCollections.observableArrayList());
            super.setList(mlc);
        }
    }

    @Override
    public void setArchiveList() {

    }

    /**
     * Updates saveableNote object when user is modifying selected note. Encodes the note to hold styling information.
     */
    @Override
    public void handleNote() {
        super.handleNote(mlc);
        setList();
    }

    /**
     * Shows a alert to the user and removes selected note from the list in view. Updates the list accordingly.
     */
    @Override
    public void handleDelete() {
        Alert a = super.handleDelete(mlc);
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        if (a.getResult() == ButtonType.OK) {
            new Toast().showToast(mlc.getMain().getPrimaryStage(),
                    mlc.getSelectedNote().getTitle() + " " + bundler.getString("succDel"),
                    Color.LIGHTGREEN, 2500, 500, 500
            );

            mlc.getNoteList().remove(mlc.getSelectedNote());
            filehandler.removeFile(mlc.getSelectedNote());
            setList();
            if (mlc.getNotes().size() > 0) {
                mlc.selectNote(0);
            } else {
                mlc.clearNoteSelection();
            }
            mlc.disableProps();
        }
    }


}
