package noteApp.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import noteApp.controller.Controller;
import noteApp.model.Context;
import noteApp.model.savestate.SaveProperties;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller for the settings layout
 *
 * @author Eric Keränen
 */
public class SettingsRootLayoutController {

    /**
     * We need a reference to the stage that it can be closed if needed
     */
    private Stage dialogStage;
    private RootLayoutController rootLayoutController;
    private String themeColor, highlightColor;
    /**
     * Remember variables to store the current setting if the user decides to cancel applying settings
     */
    private String rememberTheme, rememberHighlight;
    private String displayLanguage, rememberDisplayLanguage;
    /**
     * Index of the language in the Context singleton
     * @see noteApp.model.Context
     */
    private int currentIndex, rememberIndex;
    /**
     * Determines if the settings have been applied
     */
    private boolean isApplied;
    private Context context;
    private Locale currentLocale;
    private Controller controller;

    @FXML
    private BorderPane borderPane;
    @FXML
    private Tab appearance;
    @FXML
    private Tab languageTab;
    @FXML
    private Label lAppearance;
    @FXML
    private Label lTheme;
    @FXML
    private Label lHighlights;
    @FXML
    private Label lLanguage;
    @FXML
    private Label lDLanguage;
    @FXML
    private RadioButton darkRadio;
    @FXML
    private RadioButton lightRadio;
    @FXML
    private RadioButton redRadio;
    @FXML
    private RadioButton greenRadio;
    @FXML
    private RadioButton blueRadio;
    @FXML
    private RadioButton yellowRadio;
    @FXML
    private RadioButton finnishRadio;
    @FXML
    private RadioButton englishRadio;
    @FXML
    private RadioButton russianRadio;
    @FXML
    private RadioButton spanishRadio;
    @FXML
    private Button applyButton;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField notesPath;
    @FXML
    private Tab fileTab;
    @FXML
    private Label filePref;
    @FXML
    private Label defPath;
    @FXML
    private Button setPath;

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setRootCon(RootLayoutController root) {
        this.rootLayoutController = root;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Initialize the settings pages with styles and the currently active settings.
     * 'Remember' -variables are for returning the settings to the original state if changes are not applied / cancelled
     *
     * @param theme     {String} color of the theme
     * @param highlight {String} color of the highlights
     * @param language  {String} setting that is currently active
     * @param index     {int} of the locale setting
     */
    public void init(String theme, String highlight, String language, int index) {

        this.dialogStage.initModality(Modality.APPLICATION_MODAL);
        isApplied = false;
        this.themeColor = this.rememberTheme = theme;
        this.highlightColor = this.rememberHighlight = highlight;
        this.displayLanguage = this.rememberDisplayLanguage = language;
        this.currentIndex = this.rememberIndex = index;
        setThemeAndHighlightSettings();

        switch (themeColor) {
            case "dark":
                darkRadio.fire();
                break;
            case "light":
                lightRadio.fire();
                break;
        }
        switch (highlightColor) {
            case "red":
                redRadio.fire();
                break;
            case "green":
                greenRadio.fire();
                break;
            case "blue":
                blueRadio.fire();
                break;
            case "yellow":
                yellowRadio.fire();
                break;
        }
        switch (displayLanguage) {
            case "fi":
                finnishRadio.fire();
                break;
            case "en":
                englishRadio.fire();
                break;
            case "ru":
                russianRadio.fire();
                break;
            case "es":
                spanishRadio.fire();
                break;
        }

        dialogStage.setOnCloseRequest(e -> {
            if (!isApplied)
                handleCancel();
        });
        checkChanges();
        notesPath.setText(SaveProperties.getPath());
    }

    /**
     * Enables the 'Apply' -button if there are changes made to the settings
     */
    private void checkChanges() {
        applyButton.setDisable(rememberTheme.equals(themeColor) &&
                rememberHighlight.equals(highlightColor) &&
                rememberDisplayLanguage.equals(displayLanguage));
    }

    @FXML
    private void handleOk() {
        controller.setOfflineNotes();
        dialogStage.close();
    }

    /**
     * Apply the currently selected options and set the remember variables to the new settings.
     */
    @FXML
    private void handleApply() {
        isApplied = true;
        rememberHighlight = highlightColor;
        rememberTheme = themeColor;
        rememberDisplayLanguage = displayLanguage;
        rememberIndex = currentIndex;
        controller.setOfflineNotes();
        checkChanges();
    }

    /**
     * Change all setting to the settings that were applied before opening settings window.
     */
    @FXML
    private void handleCancel() {
        changeThemeSettings(rememberTheme);
        changeHighlightSettings(rememberHighlight);
        setThemeAndHighlightSettings();
        changeLanguageSettings(rememberDisplayLanguage, rememberIndex);
        dialogStage.close();
    }

    /**
     * Sets the theme and the highlight options to view.
     */
    private void setThemeAndHighlightSettings() {
        borderPane.getStylesheets().clear();
        borderPane.getStylesheets().add(rootLayoutController.getMainStyle(this.themeColor));
        borderPane.getStylesheets().add(rootLayoutController.getHighlightStyle(this.highlightColor));
        checkChanges();
    }

    private void setLanguageSettings() {
        currentLocale = rootLayoutController.getLanguageLocale(this.displayLanguage);
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.MyBundle", currentLocale);
        darkRadio.setText(bundle.getString("dark"));
        lightRadio.setText(bundle.getString("light"));
        redRadio.setText(bundle.getString("hl.R"));
        greenRadio.setText(bundle.getString("hl.G"));
        blueRadio.setText(bundle.getString("hl.B"));
        yellowRadio.setText(bundle.getString("hl.Y"));
        appearance.setText(bundle.getString("appearance"));
        languageTab.setText(bundle.getString("language"));
        lAppearance.setText(bundle.getString("appearance"));
        lTheme.setText(bundle.getString("theme"));
        lHighlights.setText(bundle.getString("hl"));
        lLanguage.setText(bundle.getString("language"));
        lDLanguage.setText(bundle.getString("dLanguage"));
        okButton.setText(bundle.getString("ok"));
        cancelButton.setText(bundle.getString("cancel"));
        applyButton.setText(bundle.getString("apply"));
        fileTab.setText(bundle.getString("file"));
        filePref.setText(bundle.getString("filePref"));
        defPath.setText(bundle.getString("defPath"));
        setPath.setText(bundle.getString("setPath"));
        rootLayoutController.setMenuTexts();
        checkChanges();
    }

    /**
     * Changes the theme of the application
     *
     * @param color {String} new color theme of the application
     */
    private void changeThemeSettings(String color) {
        rootLayoutController.setThemeColor(color);
        rootLayoutController.setThemeAndHighlight();
        themeColor = color;
        setThemeAndHighlightSettings();
    }

    /**
     * Changes the highlights of the application
     *
     * @param color {String} new highlight color of the application
     */
    private void changeHighlightSettings(String color) {
        rootLayoutController.setHighlightColor(color);
        rootLayoutController.setThemeAndHighlight();
        highlightColor = color;
        setThemeAndHighlightSettings();
    }

    /**
     * Changes the language of the application
     *
     * @param language {String} new display language
     * @param index Index of language
     */
    private void changeLanguageSettings(String language, int index) {
        Context.getInstance().setCurrentLocale(index);
        rootLayoutController.setLanguage(language);
        displayLanguage = language;
        currentIndex = index;
        setLanguageSettings();
    }

    /**
     * Opens a file manager where user can set default path where notes are to be saved in offline mode.
     * @param e {ActionEvent} button click event.
     */
    @FXML
    private void handleDefaultPath(ActionEvent e) {
        e.consume();
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File("src"));
        File selectedDirectory = chooser.showDialog(null);

        if (selectedDirectory != null) {
            SaveProperties.create(selectedDirectory.getAbsolutePath());
            notesPath.setText(SaveProperties.getPath());
        }
    }

    @FXML
    private void handleDarkRadio() {
        changeThemeSettings("dark");
    }

    @FXML
    private void handleLightRadio() {
        changeThemeSettings("light");
    }

    @FXML
    private void handleRedRadio() {
        changeHighlightSettings("red");
    }

    @FXML
    private void handleGreenRadio() {
        changeHighlightSettings("green");
    }

    @FXML
    private void handleBlueRadio() {
        changeHighlightSettings("blue");
    }

    @FXML
    private void handleYellowRadio() {
        changeHighlightSettings("yellow");
    }

    @FXML
    private void handleFinnishRadio() {
        changeLanguageSettings("fi", 0);
    }

    @FXML
    private void handleEnglishRadio() {
        changeLanguageSettings("en", 1);
    }

    @FXML
    private void handleRussianRadio() {
        changeLanguageSettings("ru", 2);
    }

    @FXML
    private void handleSpanishRadio() {
        changeLanguageSettings("es", 3);
    }
}
