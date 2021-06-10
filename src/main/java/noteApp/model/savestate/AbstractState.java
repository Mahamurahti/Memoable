package noteApp.model.savestate;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import noteApp.model.Context;
import noteApp.model.Filehandler;
import noteApp.model.note.Note;
import noteApp.view.MainLayoutController;

import java.io.File;
import java.util.Date;
import java.util.ResourceBundle;

import static noteApp.model.DocumentHandler.encode;

/**
 * Template used in different states of the software. Main functionality of different methods are declared here.
 */
public abstract class AbstractState implements SaveState {

    /**
     * Method which adds a note list to main view.
     *
     * @param mlc reference to {@link MainLayoutController}
     */
    public void setList(MainLayoutController mlc) {
        mlc.getNotes().addAll(mlc.getNoteList());

        Platform.runLater(() -> {
            if (mlc.getNotes().size() > 0) {
                switch (mlc.getSortingType()) {
                    case NONE:
                        mlc.getSavedNoteView().setItems(mlc.getNotes());
                        break;
                    case DESCENDINGDATE:
                        mlc.sortNotesByDescDate();
                        break;
                    case ASCENDINGDATE:
                        mlc.sortNotesByAscDate();
                        break;
                    case DESCENDINGNAME:
                        mlc.sortNotesByName();
                        break;
                    case DESCENDINGTAG:
                        mlc.sortNotesByDescTag();
                        break;
                }
            } else {
                mlc.setEmptyList();
            }
        });

        mlc.selectNote(mlc.getSavedNoteView().getSelectionModel().getSelectedIndex());

        ObservableList<String> labels = mlc.getComboBoxLabel().getItems();

        for (Note note : mlc.getNoteList()) {
            if (note.getLabel().strip().equals(""))
                continue;
            if (!(labels.contains(note.getLabel()))) {
                labels.add(note.getLabel());
            }
        }
        mlc.getComboBoxLabel().setItems(labels);

        mlc.startListening();
    }

    /**
     * Updates saveableNote object when user is modifying selected note. Encodes the note to hold styling information.
     *
     * @param mlc MainLayoutController which methods are used in modifying the note.
     */
    public void handleNote(MainLayoutController mlc) {
        mlc.setSaveableNote(mlc.getSelectedNote());

        encode(mlc.getNoteArea(), mlc.getSaveableNote());

        mlc.getSaveableNote().setTitle(mlc.getSelectedNote().getTitle());
        mlc.getSaveableNote().setLabel(mlc.getSelectedNote().getLabel());
        mlc.getSaveableNote().setDate(new Date());
    }

    /**
     * Method that shows an alert to the user when a delete button is pressed.
     *
     * @param mlc MainLayoutController used to get styling.
     * @return Alert object.
     */
    public Alert handleDelete(MainLayoutController mlc) {
        Alert a = null;
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        if (mlc.getSelectedNote() != null) {
            a = new Alert(Alert.AlertType.CONFIRMATION);
            DialogPane d = a.getDialogPane();
            d.getStylesheets().add(mlc.getRootLayoutController().getMainStyle(mlc.getRootLayoutController().getThemeColor()));
            d.getStylesheets().add(mlc.getRootLayoutController().getHighlightStyle(mlc.getRootLayoutController().getHighlightColor()));
            ((Button) d.lookupButton(ButtonType.OK)).setText((bundler.getString("ok")));
            ((Button) d.lookupButton(ButtonType.CANCEL)).setText((bundler.getString("cancel")));
            a.setTitle(bundler.getString("confirmDel"));
            a.setHeaderText(bundler.getString("sureDel") + " " + mlc.getSelectedNote().getTitle() + "?");
            a.showAndWait();
        }
        return a;
    }

    /**
     * Gets notes from folder which user has defined in settings as default save location.
     */
    @Override
    public void getNotesFromDisk(Filehandler filehandler, MainLayoutController mlc) {
        if (!SaveProperties.getPath().equals("")) {
            File folder = new File(SaveProperties.getPath());
            if (folder.listFiles() != null) {
                for (File file : folder.listFiles()) {
                    Note note = filehandler.decodeFiles(file);
                    mlc.getNoteList().add(note);
                    setList();
                }
            } else {
                SaveProperties.create("");
            }
        }
    }
}
