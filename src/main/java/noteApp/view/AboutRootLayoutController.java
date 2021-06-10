package noteApp.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URI;

/**
 * Controller for the about layout
 *
 * @author Eric Keränen
 */
public class AboutRootLayoutController {

    private Stage dialogStage;
    public void setDialogStage(Stage stage) { this.dialogStage = stage; }

    private RootLayoutController rootLayoutController;
    public void setRootCon(RootLayoutController root) { this.rootLayoutController = root; }

    private String themeColor, highlightColor;

    @FXML
    private Hyperlink link;
    private final String gitLink = "https://gitlab.metropolia.fi/nicoja/ohjelmistotuotantoprojekti-1";

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane pane;
    @FXML
    private ScrollPane scrollPane;

    /**
     * Initialize the about page with styles and set the link.
     *
     * @param theme {String} color of the theme
     * @param highlight {String} color of the highlights
     */
    public void init(String theme, String highlight){
        this.dialogStage.setResizable(false);
        this.themeColor = theme;
        this.highlightColor = highlight;
        anchorPane.getStylesheets().add(rootLayoutController.getMainStyle(this.themeColor));
        anchorPane.getStylesheets().add(rootLayoutController.getHighlightStyle(this.highlightColor));

        Platform.runLater(() -> scrollPane.setVvalue(0));

        link.setOnAction(e -> {
            try{
                Desktop.getDesktop().browse(new URI(gitLink));
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }
}
