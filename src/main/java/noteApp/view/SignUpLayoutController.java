package noteApp.view;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import noteApp.controller.Controller;

/**
 * Controller for the sign up view layout
 *
 * @author Eric Keränen
 */
public class SignUpLayoutController {

    /**
     * Link to the root of the Signing
     */
    private SignRootLayoutController signRootController;
    private Controller controller;
    public void initRootController(SignRootLayoutController controller) {
        signRootController = controller;
    }

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField repeatPassword;

    /**
     * Registers the user into a database. If the passwords do not match, show an error message and if they match
     * continue with the registration and automatically sign in the new user.
     *
     * @param e ActionEvent
     */
    @FXML
    private void handleSignUp(ActionEvent e){
        e.consume();
        System.out.printf("Button: Sign Up: username: %s, password: %s\n",
                username.getText(), password.getText());
        if (!password.getText().equals(repeatPassword.getText())){
            new Toast().showToast(this.signRootController.getDialogStage(),
                    "Passwords must match", Color.RED, 3500, 500, 500);
        } else {
            try {
                if (controller.signUp(username.getText(), password.getText())){
                    this.signRootController.getRootCon().getUserMenu().setText(username.getText());
                    this.signRootController.getRootCon().getUserMenu().getItems().set(0, this.signRootController.generateSignOut());
                    this.controller.startAutoSaveThread();
                    this.signRootController.closeDialog();
                }
            } catch (Exception exception) {
                new Toast().showToast(this.signRootController.getDialogStage(),
                        exception.getLocalizedMessage(), Color.RED, 3500, 500, 500);
            }
        }
    }

    /**
     * Handles the "Back" -buttons action by loading the "Sign In" view
     *
     * @param e {ActionEvent} is the action of the event
     */
    @FXML
    private void handleBackToLogin(ActionEvent e){
        e.consume();

        signRootController.loadSignIn();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
