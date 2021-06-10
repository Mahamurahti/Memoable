package noteApp.model.note;

import noteApp.controller.Controller;
import noteApp.model.Context;
import noteApp.view.MainLayoutController;
import noteApp.view.RootLayoutController;
import noteApp.view.Toast;
import javafx.scene.paint.Color;
import javafx.application.Platform;

import java.util.ResourceBundle;

/**
 * Custom thread that handles saving feature. Contains a timer when reset uses save feature to get recent user made
 * changes from the view.
 * @author Matias Vainio
 */
public class AutoSaveThread implements Runnable {
    private static Thread thread;
    private final MainLayoutController mlc;
    private final RootLayoutController rlc;
    private final Controller controller;
    private boolean done;
    private int sleepTime;

    public AutoSaveThread(RootLayoutController rlc, MainLayoutController mlc, Controller controller) {
        this.mlc = mlc;
        this.controller = controller;
        this.done = false;
        this.sleepTime = 0;
        this.rlc = rlc;
    }

    /**
     * Returns this thread.
     * @return thread to be returned.
     */
    public static Thread getThread() {
        return thread;
    }

    /**
     * Run method acts as a timer to the auto saving feature. The thread is interrupted every time user does something
     * to the selected note in the view. When user stops making adjustments the sleep timer starts and after 2 seconds
     * saving happens.
     */
    @Override
    public void run() {
        while (done) {
            this.mlc.disableList(true);
            try {
                Thread.sleep(sleepTime);
                this.mlc.disableList(false);
                if (mlc.getIsModified()) {
                    save(this.mlc.getSaveableNote());
                }
                mlc.setIsModified(false);
            } catch (InterruptedException e) {
                continue;
            }
            if (sleepTime == 0) sleepTime = 2000;
        }
    }

    /**
     * Disables auto saving.
     */
    public void cancel() {
        this.done = false;
    }

    /**
     * Enables auto svaing.
     */
    public void enable() {
        this.done = true;
    }

    /**
     * Starts thread that handles auto save feature.
     */
    public void startThread() {
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        new Toast().showToast(this.mlc.getMain().getPrimaryStage(),
                bundler.getString("login"), Color.LIGHTGREEN, 3500, 500, 500);
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        this.mlc.clearDisableWrapper();
    }

    /**
     * Saves provided note to the database.
     *
     * @param note {Note} note to be saved.
     */
    public void save(Note note) {
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", rlc.getLanguageLocale(rlc.getLanguage()));
        if (note != null) {
            try {
                controller.updateNote(note);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                new Toast().showToast(this.mlc.getMain().getPrimaryStage(),
                        note.getTitle() + " " + bundler.getString("saved"),
                        Color.LIGHTGREEN, 1500, 500, 500);
            });
            if (mlc.getTitle().getText().equals(bundler.getString("archive"))) {
                controller.getState().setArchiveList();
            } else {
                controller.getState().setList();
            }
        }
    }
}
