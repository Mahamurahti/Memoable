package noteApp.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Class that displays a "toast" message in the bottom of the stage that called the toast
 *
 * @author Eric Keränen
 */
public class Toast {

    /**
     * Shows a "toast" message at the bottom of the screen. The message's properties can be changed with the parameters.
     * After fading out, closes the toast message.
     *
     * @param ownerStage {Stage} where the message will be displayed
     * @param msg {String} of the displayed message
     * @param color {Color} of the displayed messages text
     * @param delay {int} time for how long the message sticks around
     * @param fadeInDelay {int} time for how long until the messages opacity is 1
     * @param fadeOutDelay {int} time for how long until the messages opacity is 0
     */
    public void showToast(Stage ownerStage, String msg, Color color, int delay, int fadeInDelay, int fadeOutDelay) {
        //Stage stage = new Stage();
        Popup popup = new Popup();
        // Set the display message background width to be dynamic
        popup.setWidth(msg.length() * 17);
        popup.setHeight(20);

        // Set the message to the middle of the stage and to the bottom
        popup.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - popup.getWidth() / 2);
        popup.setY(ownerStage.getY() + ownerStage.getHeight() - 80);

        Text text = new Text(msg);
        text.setFont(Font.font("Arial", 28));
        text.setFill(color);

        StackPane root = new StackPane(text);
        root.setStyle(
                "-fx-background-radius: 20;" +
                "-fx-background-color: rgba(0, 0, 0, 0.4);" +
                "-fx-padding: 10px 25px;"
        );
        root.setOpacity(0);

        // Hide the popup if clicked
        root.setOnMouseClicked(e -> popup.hide());

        popup.getContent().addAll(root);

        popup.show(ownerStage);
        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey = new KeyFrame(Duration.millis(fadeInDelay), new KeyValue(popup.getContent().get(0).opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey);
        // After fade in is finished, wait and then play fade out animation
        
        fadeInTimeline.setOnFinished((ae) -> {
            new Thread(() -> {
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Timeline fadeOutTimeline = new Timeline();
                KeyFrame fadeOutKey = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue(popup.getContent().get(0).opacityProperty(), 0));
                fadeOutTimeline.getKeyFrames().add(fadeOutKey);
                fadeOutTimeline.setOnFinished((aeb) -> popup.hide());
                fadeOutTimeline.play();
            }).start();
        });
        // Start fade in animation
        fadeInTimeline.play();
    }
}
