package noteApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import noteApp.controller.Controller;
import noteApp.controller.ControllerImpl;
import noteApp.model.Context;
import noteApp.view.MainLayoutController;
import noteApp.view.RootLayoutController;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Main class of the application. Initializes the user interface.
 *
 * @author Eric Keranen
 */
public class Main extends Application {

    /**
     * How many iterations happen in the hashing process
     */
    public static final int HASH_ITERATIONS = 100_000;

    private Controller controller;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private AnchorPane mainLayout;
    private RootLayoutController rlc;
    private MainLayoutController mlc;

    /**
     * Starts the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX start method 
     * <p>
     * Starts the application's frontend
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Note App");
        this.primaryStage.setMinWidth(500);
        this.primaryStage.setMinHeight(400);

        initRoot();
        initMain();

        this.controller = new ControllerImpl(rlc, mlc);

        rlc.setCon(controller);
        mlc.setCon(controller);
        rlc.setMlc(mlc);

        mlc.setPlus();

        // Show login window on startup
        rlc.initSignRoot();
    }

    /**
     * Initializes the RootLayout of the application
     */
    public void initRoot() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml-files/RootLayout.fxml"));
            loader.setResources(ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale()));
            rootLayout = loader.load();
            rlc = loader.getController();
            Scene scene = new Scene(rootLayout, 1200, 600);
            // Default theme and highlights
            scene.getStylesheets().add(getClass().getResource("/stylesheets/light_theme.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/stylesheets/yellow_highlight.css").toExternalForm());
            primaryStage.setScene(scene);

            primaryStage.setOnCloseRequest((e) -> {
                System.exit(0);
            });

            rlc.init();

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the Main layout of the applicatio on top of the RootLayout
     */
    public void initMain() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml-files/MainLayout.fxml"));
            loader.setResources(ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale()));
            mainLayout = loader.load();
            mlc = loader.getController();
            mlc.setRootLayoutController(rlc);
            mlc.setMain(this);
            rootLayout.setCenter(mainLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }
}
