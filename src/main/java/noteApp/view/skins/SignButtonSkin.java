package noteApp.view.skins;

import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.util.Duration;

/**
 * Button skin that executes animations in login,
 *
 * @author Eric Keränen
 */
public class SignButtonSkin extends ButtonSkin {

    public SignButtonSkin(Button control){
        super(control);

        final TranslateTransition transitionUp = new TranslateTransition(Duration.millis(200), control);
        transitionUp.setToY(-3);
        control.setOnMouseEntered(e -> transitionUp.playFromStart());

        final TranslateTransition transitionDown = new TranslateTransition(Duration.millis(200), control);
        transitionDown.setToY(0);
        control.setOnMouseExited(e -> transitionDown.playFromStart());
    }

}
