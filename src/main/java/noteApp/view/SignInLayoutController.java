package noteApp.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import noteApp.controller.Controller;
import noteApp.model.savestate.OfflineState;

/**
 * Controller for the sign in view layout
 *
 * @author Eric Keränen
 */
public class SignInLayoutController {

    private Controller con;
    /**
     * Link to the root of the Signing
     */
    private SignRootLayoutController signRootController;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    public void setCon(Controller con) {
        this.con = con;
    }

    public void initRootController(SignRootLayoutController controller) {
        signRootController = controller;
    }

    /**
     * Tries to sign in the user. If the logging in fails, display an error message and if it succeeds, start autosave
     * thread and generate a sign out button under the users tab.
     *
     * @param e Actionevent
     */
    @FXML
    private void handleSignIn(ActionEvent e) {
        e.consume();
        if (con.logIn(username.getText(), password.getText())) {
            this.signRootController.getRootCon().getUserMenu().setText(username.getText());
            this.signRootController.getRootCon().getUserMenu().getItems().set(0, this.signRootController.generateSignOut());
            this.con.startAutoSaveThread();
            this.signRootController.closeDialog();
        } else {
            new Toast().showToast(this.signRootController.getDialogStage(),
                    "Incorrect username or password", Color.RED, 3500, 500, 500);
        }
    }

    /**
     * Refreshes the language of the sign out button
     */
    public void refreshSignOut(){
        this.signRootController.getRootCon().getUserMenu().getItems().set(0, this.signRootController.generateSignOut());
    }

    /**
     * Option to continue using the application offline
     */
    @FXML
    private void handleContinue() {
        // Set save state.
        this.con.setState(new OfflineState(this.con.getMlc()));
        this.con.toggleProps();
        this.signRootController.closeDialog();
    }

    /**
     * Handles the "Sign Up" -buttons action by loading the "Sign Up" view
     *
     * @param e {ActionEvent} is the action of the event
     */
    @FXML
    private void handleSignUp(ActionEvent e) {
        e.consume();

        signRootController.loadSignUp();
    }

    /**
     * Handles the "Back" -buttons action by loading the "Sign In" view
     *
     * @param e {ActionEvent} is the action of the event
     */
    @FXML
    private void handleBackToNotes(ActionEvent e) {
        e.consume();
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }

}
