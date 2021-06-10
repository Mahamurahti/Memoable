package noteApp.view.listCell;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import noteApp.model.note.Note;
import noteApp.view.MainLayoutController;
import org.apache.shiro.codec.Hex;

import java.text.ParseException;

/**
 * Main layout custom list cell
 *
 * @param <T> type of the Class
 */
public class DefaultListCell<T> extends ListCell<T> {
    private final Button button = new Button();
    private final Circle priority = new Circle();
    private Note latestNote = null;
    private final MainLayoutController mlc;
    private final ListView listView;

    public <T> DefaultListCell(MainLayoutController mlc, ListView<T> listView) {
        this.mlc = mlc;
        this.listView = listView;
    }

    /**
     * Sets a small ball on the list cell to indicate the priority of the note and a delete button
     * to delete the note.
     *
     * @param priorityLevel {String} css class for the ball that will determine the ball's color
     * @return {HBox} horizontal box, that contains the ball and the button with a spacing of 5
     */
    private Node setPrioAndBtn(String priorityLevel) {
        priority.setRadius(5);
        switch (priorityLevel) {
            case "high":
                priority.setFill(Color.web("#eb3528"));
                break;
            case "medium":
                priority.setFill(Color.web("#ebc53f"));
                break;
            case "low":
                priority.setFill(Color.web("#2ce31b"));
                break;
            case "none":
                priority.setFill(Color.TRANSPARENT);
                break;
        }
        priority.translateYProperty().set(9);
        priority.setOpacity(.8);

        button.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.TRASH, "12px"));
        button.getStyleClass().add("deleteBtn");
        button.setOnAction(a -> {
            this.mlc.setSelectedNote(latestNote);
            this.mlc.handleDelete(new ActionEvent());
        });

        return new HBox(5, priority, button);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (item instanceof Node) {
            setText(null);
            Node currentNode = getGraphic();
            Node newNode = (Node) item;
            if (currentNode == null || !currentNode.equals(newNode)) {
                setGraphic(newNode);
            }
        } else {
            Note note = (Note) item;
            try {
                setText(item == null ? "null" : note + "\n" + note.getLocalizedDateString(mlc.getRootLayoutController().getLanguage()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setMinWidth(listView.getWidth() - 80);
            setMaxWidth(listView.getWidth() - 80);
            setPrefWidth(listView.getWidth() - 80);
            setWrapText(true);
            if(item != null)
                setGraphic(setPrioAndBtn(note.getTag().toString().toLowerCase()));
            latestNote = (Note) item;
        }
    }
}
