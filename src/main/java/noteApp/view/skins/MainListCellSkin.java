package noteApp.view.skins;

import javafx.scene.control.ListCell;
import javafx.scene.control.skin.ListCellSkin;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

/**
 * ListCell skin that executes animations in main.
 *
 * @author Eric Keränen
 */
public class MainListCellSkin extends ListCellSkin<String> {

    public MainListCellSkin(ListCell<String> control) {
        super(control);

        final TranslateTransition transRight = new TranslateTransition(Duration.millis(200), control);
        transRight.setToX(10);
        control.setOnMouseEntered(e -> transRight.playFromStart());

        final TranslateTransition transBack = new TranslateTransition(Duration.millis(200), control);
        transBack.setToX(1);
        control.setOnMouseExited(e -> transBack.playFromStart());
    }
}
