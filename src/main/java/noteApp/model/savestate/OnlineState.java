package noteApp.model.savestate;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import noteApp.controller.Controller;
import noteApp.model.Context;
import noteApp.model.note.AutoSaveThread;
import noteApp.model.note.Note;
import noteApp.view.MainLayoutController;
import noteApp.view.Toast;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * A state in program where online functionalities are enabled. Note CRUD operations are done to the database.
 *
 * @author Matias Vainio
 */
public class OnlineState extends AbstractState {
    private final Controller controller;
    private final MainLayoutController mlc;
    /**
     * ResourceBundle used to get localizations from properties file.
     */
    private final ResourceBundle bundler;


    public OnlineState(Controller controller, MainLayoutController mlc) {
        this.controller = controller;
        this.mlc = mlc;
        bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
    }

    /**
     * Creates a new Note object, saves it to the database and adds it to the list in the view.
     *
     * @param title   {String} title of the note.
     * @param content {String} content of the note.
     */
    @Override
    public void createNote(String title, String content) {
        try {
            controller.saveToDatabase(title, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setList();
        this.mlc.selectNote(mlc.getSavedNoteView().getItems().size());
        this.mlc.disableProperties();
    }

    /**
     * Retrieves saved notes from the database and sets them in the list in view.
     */
    @Override
    public void setList() {
        if (this.mlc != null) {
            mlc.stopListening();
            mlc.setNotes(FXCollections.observableArrayList());
            List<Note> notes = null;
            try {
                notes = controller.getList()
                        .stream()
                        .filter(n -> !n.getTitle().contains("%a"))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                new Toast().showToast(mlc.getMain().getPrimaryStage(),
                        mlc.getSelectedNote().getTitle() + " " + bundler.getString("somWrong"),
                        Color.RED, 2500, 500, 500
                );
            }
            mlc.setNoteList(notes);
            super.setList(mlc);
        }
    }

    /**
     * Sets archived notes to the listview.
     */
    @Override
    public void setArchiveList() {
        if (this.mlc != null) {
            mlc.stopListening();
            mlc.setNotes(FXCollections.observableArrayList());
            List<Note> notes = null;
            try {
                notes = controller.getList()
                        .stream()
                        .filter(n -> n.getTitle().contains("%a"))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                new Toast().showToast(mlc.getMain().getPrimaryStage(),
                        mlc.getSelectedNote().getTitle() + " " + bundler.getString("somWrong"),
                        Color.RED, 2500, 500, 500
                );
            }
            mlc.setNoteList(notes);
            super.setList(mlc);
        }
    }

    /**
     * Updates saveableNote object when user is modifying selected note. Encodes the note to hold styling information.
     * Starts thread that handles auto saving notes to the database.
     */
    @Override
    public void handleNote() {
        super.handleNote(mlc);
        if (mlc.getSaveableNote() != null) {
            try {
                mlc.setIsModified(true);
                AutoSaveThread.getThread().interrupt();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Shows a alert to the user and removes selected note from the list in view and from the database.
     * Updates the list accordingly.
     */
    @Override
    public void handleDelete() {
        Alert a = super.handleDelete(mlc);
        if (a.getResult() == ButtonType.OK) {
            new Toast().showToast(mlc.getMain().getPrimaryStage(),
                    mlc.getSelectedNote().getTitle() + " " + bundler.getString("succDel"),
                    Color.LIGHTGREEN, 2500, 500, 500
            );

            try {
                controller.deleteFromDatabase(mlc.getSelectedNote());
            } catch (Exception e) {
                new Toast().showToast(mlc.getMain().getPrimaryStage(),
                        mlc.getSelectedNote().getTitle() + " " + bundler.getString("errDel"),
                        Color.RED, 2500, 500, 500
                );
            }
            setList();
            if (mlc.getNotes().size() > 0) {
                mlc.selectNote(0);
            } else {
                mlc.clearNoteSelection();
            }
            mlc.disableProperties();
        }
    }
}
