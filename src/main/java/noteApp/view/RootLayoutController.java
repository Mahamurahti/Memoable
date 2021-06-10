package noteApp.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import noteApp.controller.Controller;
import noteApp.model.Context;
import noteApp.model.note.Note;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Controller for the root layout
 * 
 * @author Eric Keränen
 * @author Matias Vainio
 * @author Jere Salmensaari
 * @author Teemu Viljanen
 * @author Nico Järvinen
 */
public class RootLayoutController {

    /**
     * KeyControl is for the commands in the "Edit" menu
     */
    private final KeyControl keyControl = new KeyControl();
    /**
     * White theme and yellow highlights are default
     */
    public String highlightColor = "yellow", themeColor = "light";
    /**
     * English language is default
     */
    public String language = "en";
    /**
     * Index of the language
     */
    public int index = 1;
    private BorderPane settingRootLayout;
    private BorderPane signRootLayout;
    private SignRootLayoutController signRootController;
    private AnchorPane aboutRootLayout;
    private MainLayoutController mlc;
    private Controller con;
    /**
     * The default path for saving notes locally
     */
    private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
    private String path = "";
    /**
     * No selected note is default
     */
    private Note selectedNote = null;
    private FXMLLoader loader;

    @FXML
    private Menu fileMenu;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu viewMenu;
    @FXML
    private Menu changeViewMenu;
    @FXML
    private Menu themeChangeMenu;
    @FXML
    private Menu hlChangeMenu;
    @FXML
    private Menu userMenu;
    @FXML
    private Menu helpMenu;
    @FXML
    private BorderPane borderPane;
    @FXML
    private RadioMenuItem defaultView;
    @FXML
    private MenuItem saveAs;
    @FXML
    private MenuItem add;
    @FXML
    private MenuItem settings;
    @FXML
    private MenuItem exit;
    @FXML
    private MenuItem copy;
    @FXML
    private MenuItem paste;
    @FXML
    private MenuItem cut;
    @FXML
    private MenuItem undo;
    @FXML
    private MenuItem redo;
    @FXML
    private MenuItem delete;

    @FXML
    private RadioMenuItem light;
    @FXML
    private RadioMenuItem dark;

    @FXML
    private RadioMenuItem red;
    @FXML
    private RadioMenuItem green;
    @FXML
    private RadioMenuItem blue;
    @FXML
    private RadioMenuItem yellow;
    @FXML
    private RadioMenuItem archive;
    @FXML
    private MenuItem signIn;
    @FXML
    private MenuItem about;

    public void setCon(Controller con) {
        this.con = con;
    }

    public MainLayoutController getMlc() {
        return this.mlc;
    }

    public void setMlc(MainLayoutController mlc) {
        this.mlc = mlc;
    }

    public void init() {
        defaultView.setSelected(true);
    }

    /**
     * Handles the "Save As" function, by saving the note locally
     *
     * @param e {ActionEvent} is the action of the event
     * @throws IOException path is invalid
     */
    @FXML
    private void handleSaveAs(ActionEvent e) throws IOException {
        e.consume();

        saveAs();
    }

    /**
     * Handles the "Save As" function, by saving the note locally.
     * This method is separate since other function call it.
     *
     * @throws IOException path is invalid
     */
    public void saveAs() throws IOException {
        mlc.saveFile();
    }

    /**
     * Adds a file from the computer to the app view
     *
     * @param e {ActionEvent} is the action of the event
     */
    @FXML
    private void handleAdd(ActionEvent e) {
        e.consume();
        mlc.getFile();
    }

    /**
     * Initializes and opens the settings view.
     *
     * @param e {ActionEvent} is the action of the event
     */
    @FXML
    private void handleSettings(ActionEvent e) {
        e.consume();
        initSettingsRoot();
    }

    @FXML
    private void handleExit(ActionEvent e) {
        e.consume();
        System.exit(0);
    }

    /**
     * Sets the default save path for notes that are saved locally
     *
     * @param e {ActionEvent} is the action of the event
     */
    @FXML
    private void handleDefaultPath(ActionEvent e) {
        e.consume();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select default saving path");

        File file = chooser.showDialog(null);

        prefs.put("defpath", file.getAbsolutePath());
    }

    @FXML
    private void handleCopy(ActionEvent e) {
        e.consume();
        keyControl.copy();
    }

    @FXML
    private void handlePaste(ActionEvent e) {
        e.consume();
        keyControl.paste();
    }

    @FXML
    private void handleCut(ActionEvent e) {
        e.consume();
        keyControl.cut();
    }

    @FXML
    private void handleUndo(ActionEvent e) {
        e.consume();
        keyControl.undo();
    }

    @FXML
    private void handleRedo(ActionEvent e) {
        e.consume();
        keyControl.redo();
    }

    @FXML
    private void handleDelete(ActionEvent e) {
        e.consume();

        mlc.handleDelete(new ActionEvent());
    }

    /**
     * Initializes and opens the about view
     *
     * @param e {ActionEvent} is the action of the event
     */
    @FXML
    private void handleAbout(ActionEvent e) {
        e.consume();
        initAboutRoot();
    }

    public String getThemeColor() {
        return this.themeColor;
    }

    public void setThemeColor(String color) {
        this.themeColor = color;
    }

    public String getHighlightColor() {
        return this.highlightColor;
    }

    public void setHighlightColor(String color) {
        this.highlightColor = color;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Locale getLanguageLocale(String language) {
        switch (language) {
            case "fi":
                return new Locale("fi");
            case "en":
                return new Locale("en");
            case "ru":
                return new Locale("ru");
            case "es":
                return new Locale("es");
        }
        return null;
    }

    /**
     * Method for getting the correct theme file for layouts
     *
     * @param theme {String} is the name of the theme
     * @return file {String} that corresponds the style
     */
    public String getMainStyle(String theme) {
        switch (theme) {
            case "dark":
                return getClass().getResource("/stylesheets/dark_theme.css").toExternalForm();
            case "light":
                return getClass().getResource("/stylesheets/light_theme.css").toExternalForm();
        }
        return null;
    }

    /**
     * Method for getting the correct highlight file for layouts
     *
     * @param highlight {String} is the name of the highlight
     * @return file {String} that corresponds the highlight
     */
    public String getHighlightStyle(String highlight) {
        switch (highlight) {
            case "red":
                return getClass().getResource("/stylesheets/red_highlight.css").toExternalForm();
            case "green":
                return getClass().getResource("/stylesheets/green_highlight.css").toExternalForm();
            case "blue":
                return getClass().getResource("/stylesheets/blue_highlight.css").toExternalForm();
            case "yellow":
                return getClass().getResource("/stylesheets/yellow_highlight.css").toExternalForm();
        }
        return null;
    }

    /**
     * Clears all css and sets the theme and highlight.
     */
    public void setThemeAndHighlight() {
        borderPane.getStylesheets().clear();
        borderPane.getStylesheets().add(getMainStyle(themeColor));
        borderPane.getStylesheets().add(getHighlightStyle(highlightColor));
    }

    @FXML
    private void handleLightTheme(ActionEvent e) {
        e.consume();
        themeColor = "light";
        setThemeAndHighlight();
    }

    @FXML
    private void handleDarkTheme(ActionEvent e) {
        e.consume();
        themeColor = "dark";
        setThemeAndHighlight();
    }

    @FXML
    private void handleRedHighlight(ActionEvent e) {
        e.consume();
        highlightColor = "red";
        setThemeAndHighlight();
    }

    @FXML
    private void handleGreenHighlight(ActionEvent e) {
        e.consume();
        highlightColor = "green";
        setThemeAndHighlight();
    }

    @FXML
    private void handleBlueHighlight(ActionEvent e) {
        e.consume();
        highlightColor = "blue";
        setThemeAndHighlight();
    }

    @FXML
    private void handleYellowHighlight(ActionEvent e) {
        e.consume();
        highlightColor = "yellow";
        setThemeAndHighlight();
    }

    /**
     * Initializes and opens the Signing view.
     *
     * @param e {ActionEvent} is the action of the event
     */
    @FXML
    private void handleSignIn(ActionEvent e) {
        e.consume();

        initSignRoot();
    }

    public Menu getFileMenu() {
        return this.fileMenu;
    }

    public MenuItem getFileMenu(int index) {
        return this.fileMenu.getItems().get(index);
    }

    public Menu getEditMenu() {
        return this.editMenu;
    }

    public Menu getViewMenu() {
        return this.viewMenu;
    }

    public Menu getChangeViewMenu() {
        return this.changeViewMenu;
    }

    public Menu getUserMenu() {
        return this.userMenu;
    }

    public Menu getHelpMenu() {
        return this.helpMenu;
    }

    public void setMenuTexts() {
        Locale locale = getLanguageLocale(getLanguage());
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", locale);
        fileMenu.setText(bundler.getString("file"));
        editMenu.setText(bundler.getString("edit"));
        viewMenu.setText(bundler.getString("view"));
        if(this.con.isLoggedIn())
            userMenu.setText(bundler.getString("user"));
        helpMenu.setText(bundler.getString("help"));
        saveAs.setText(bundler.getString("saveas"));
        add.setText(bundler.getString("add"));
        settings.setText(bundler.getString("settings"));
        exit.setText(bundler.getString("exit"));
        light.setText(bundler.getString("light"));
        dark.setText(bundler.getString("dark"));
        red.setText(bundler.getString("hl.R"));
        green.setText(bundler.getString("hl.G"));
        blue.setText(bundler.getString("hl.B"));
        yellow.setText(bundler.getString("hl.Y"));
        signIn.setText(bundler.getString("sin"));
        about.setText(bundler.getString("about"));
        changeViewMenu.setText(bundler.getString("c.view"));
        themeChangeMenu.setText(bundler.getString("theme"));
        hlChangeMenu.setText(bundler.getString("hl"));
        defaultView.setText(bundler.getString("default"));
        archive.setText(bundler.getString("archive"));
        copy.setText(bundler.getString("copy"));
        paste.setText(bundler.getString("paste"));
        cut.setText(bundler.getString("cut"));
        undo.setText(bundler.getString("undo"));
        redo.setText(bundler.getString("redo"));
        delete.setText(bundler.getString("delete"));
        mlc.changeText();
        mlc.setList();
        signRootController.changeText();
        signRootController.getSignInLayoutController().refreshSignOut();
    }

    /**
     * Initializes SettingsRootLayoutController, which controls settings.
     */
    public void initSettingsRoot() {
        try {
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml-files/SettingsRootLayout.fxml"));
            loader.setResources(ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale()));
            settingRootLayout = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(new Scene(settingRootLayout, 600, 500));

            SettingsRootLayoutController settingsRootLayoutController = loader.getController();

            settingsRootLayoutController.setDialogStage(stage);
            settingsRootLayoutController.setRootCon(this);
            settingsRootLayoutController.setController(con);

            settingsRootLayoutController.init(themeColor, highlightColor, language, index);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes SignRootLayoutController, which controls signing in and signing up.
     */
    public void initSignRoot() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml-files/SignRootLayout.fxml"));
            loader.setResources(ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale()));
            signRootLayout = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Sign In");
            stage.setScene(new Scene(signRootLayout, 300, 500));

            signRootController = loader.getController();

            signRootController.setCon(con);
            signRootController.setDialogStage(stage);
            signRootController.setRootCon(this);

            signRootController.init(themeColor, highlightColor);
            signRootController.loadSignIn();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes AboutRootLayoutController, where you can view extra information about the application.
     */
    public void initAboutRoot() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml-files/AboutRootLayout.fxml"));
            loader.setResources(ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale()));
            aboutRootLayout = loader.load();

            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(new Scene(aboutRootLayout, 300, 500));

            AboutRootLayoutController aboutRootController = loader.getController();

            aboutRootController.setDialogStage(stage);
            aboutRootController.setRootCon(this);

            aboutRootController.init(themeColor, highlightColor);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes list view to show archived notes.
     */
    @FXML
    private void handleArchive() {
        this.mlc.setArchivedNotesToView();
    }

    /**
     * Changes list view to show default notes.
     */
    @FXML
    private void handleMainView() {
        this.mlc.setDefaultNotesToView();
    }

}
