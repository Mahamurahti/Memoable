package noteApp.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import noteApp.controller.Controller;
import noteApp.model.Context;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Controller for the root of signing in and signing up an account views.
 *
 * @author Eric Keränen
 */
public class SignRootLayoutController {

    private Controller con;
    /**
     * We need a reference to tthe Stage, that it can be closed if needed
     */
    private Stage dialogStage;
    private RootLayoutController rootLayoutController;
    private String themeColor, highlightColor;

    private SignInLayoutController signInLayoutController;
    public SignInLayoutController getSignInLayoutController(){
        return signInLayoutController;
    }

    @FXML
    private BorderPane borderPane;

    public void setCon(Controller con) {
        this.con = con;
    }

    public Stage getDialogStage() {
        return this.dialogStage;
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public RootLayoutController getRootCon() {
        return this.rootLayoutController;
    }

    public void setRootCon(RootLayoutController root) {
        this.rootLayoutController = root;
    }

    public String getThemeColor() {
        return this.themeColor;
    }

    public String getHighlightColor() {
        return this.highlightColor;
    }

    /**
     * Initializes SignRootLayoutController theme.
     *
     * @param theme {String} is the name of the theme currently in use
     * @param highlight {String} is the name of the highlight currently in use
     */
    public void init(String theme, String highlight) {
        this.dialogStage.initModality(Modality.APPLICATION_MODAL);
        this.themeColor = theme;
        this.highlightColor = highlight;
        borderPane.getStylesheets().add(rootLayoutController.getMainStyle(this.themeColor));
        borderPane.getStylesheets().add(rootLayoutController.getHighlightStyle(this.highlightColor));
    }

    /**
     * Loads the SignUpLayout for the user to sign up
     */
    public void loadSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml-files/SignUpLayout.fxml"));
            loader.setResources(ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale()));
            AnchorPane signUpLayout = loader.load();
            SignUpLayoutController signUpLayoutController = loader.getController();
            signUpLayoutController.initRootController(this);
            signUpLayoutController.setController(con);

            borderPane.setCenter(signUpLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the SignInLayout for the user to sign in
     */
    public void loadSignIn() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml-files/SignInLayout.fxml"));
            loader.setResources(ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale()));
            AnchorPane signInLayout = loader.load();
            signInLayoutController = loader.getController();
            signInLayoutController.initRootController(this);
            signInLayoutController.setCon(con);

            borderPane.setCenter(signInLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a 'Sign Out' -button for the user to sign out of their user account
     *
     * @return node {MenuItem} that has an action to sign the user out
     */
    public MenuItem generateSignOut(){
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        MenuItem menuItem = new MenuItem();
        menuItem.setText(bundler.getString("sigou"));
        menuItem.setOnAction(a -> {
            getRootCon().getMlc().clearNoteSelection();
            getRootCon().getMlc().disableProperties();
            getRootCon().getUserMenu().setText(bundler.getString("user"));
            getRootCon().getUserMenu().getItems().set(0, generateSignIn());
            new Toast().showToast(getRootCon().getMlc().getMain().getPrimaryStage(),
                    bundler.getString("logout"), Color.LIGHTGREEN, 1500, 500, 500);
            con.logOut();
        });
        return menuItem;
    }

    /**
     * Generates a 'Sign In' -button for the user to sign in to their user account
     *
     * @return node {MenuItem} that has an action to sign the user in
     */
    private MenuItem generateSignIn(){
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        MenuItem menuItem = new MenuItem();
        menuItem.setText(bundler.getString("sin"));
        menuItem.setOnAction(a -> {
            getRootCon().initSignRoot();
        });
        return menuItem;
    }

    /**
     * Refreshes the language of the 'Sign out' or 'Sign In' -buttons
     */
    public void changeText(){
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        if(this.con.isLoggedIn())
            getRootCon().getUserMenu().getItems().get(0).setText(bundler.getString("sin"));
        else
            getRootCon().getUserMenu().getItems().get(0).setText(bundler.getString("sigou"));
    }

    /**
     * Closes the SignRootLayout window
     */
    public void closeDialog() {
        this.dialogStage.close();
    }
}
