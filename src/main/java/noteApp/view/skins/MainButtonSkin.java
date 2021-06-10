package noteApp.view.skins;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.util.Duration;

/**
 * Button skin that executes animations in main.
 *
 * @author Eric Keränen
 */
public class MainButtonSkin extends ButtonSkin {

    public MainButtonSkin(Button control){
        super(control);

        final FadeTransition fadeIn = new FadeTransition(Duration.millis(100), control);
        fadeIn.setToValue(1);
        final TranslateTransition transitionUp = new TranslateTransition(Duration.millis(200), control);
        transitionUp.setToY(-3);
        control.setOnMouseEntered(e -> {
            transitionUp.playFromStart();
            fadeIn.playFromStart();
        });

        final FadeTransition fadeOut = new FadeTransition(Duration.millis(100), control);
        fadeOut.setToValue(0.8);
        final TranslateTransition transitionDown = new TranslateTransition(Duration.millis(200), control);
        transitionDown.setToY(0);
        control.setOnMouseExited(e -> {
            transitionDown.playFromStart();
            fadeOut.playFromStart();
        });

        control.setOpacity(0.8);
    }

}
