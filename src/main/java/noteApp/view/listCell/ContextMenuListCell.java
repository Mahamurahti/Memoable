package noteApp.view.listCell;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import noteApp.model.note.Note;
import noteApp.view.MainLayoutController;

/**
 * Creates a custom cell containing objects to the listview.
 * @author Matias Vainio
 */
public class ContextMenuListCell extends ListCell<Note> {

    public ContextMenuListCell(ContextMenu contextMenu) {
        setContextMenu(contextMenu);
    }

    /**
     * Used to create a cell for ListView in the {@link MainLayoutController}.
     * Cell can contain custom objects and all is displayed to the user.
     * @param mlc reference to {@link MainLayoutController}.
     * @return callback function used to create a list cell.
     */
    public static Callback<ListView<Note>, ListCell<Note>> forListView(MainLayoutController mlc) {
        return forListView(null, mlc);
    }

    /**
     * Creates a callback function which is used to create a new cell to listview.
     * @param cellFactory list cell which is used in listview.
     * @param mlc reference to {@link MainLayoutController}.
     * @return callback function used to create a list cell.
     */
    public static Callback<ListView<Note>, ListCell<Note>> forListView(final Callback<ListView<Note>, ListCell<Note>> cellFactory,
                                                                     MainLayoutController mlc) {
        return listView -> cellFactory == null ? new DefaultListCell<>(mlc, listView) : cellFactory.call(listView);
    }
}
