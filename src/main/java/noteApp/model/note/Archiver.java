package noteApp.model.note;

import noteApp.controller.Controller;
import noteApp.view.MainLayoutController;

/**
 * Note archiver class which adds or removes special characters from notes' titles. Characters are used
 * to show either normal notes or archived notes on the view.
 * @author Matias Vainio
 */
public class Archiver {
    private final Controller controller;

    public Archiver(Controller controller) {
        this.controller = controller;
    }

    /**
     * Archives provided note by appending '%a' to the start note's title. '%a' is used to check which notes are
     * going to be showed on the view. Updates the view.
     * @param selectedNote provided note which is going to be modified.
     */
    public void archiveNote(Note selectedNote) {
        String title = "%a" + selectedNote.getTitle();
        selectedNote.setTitle(title);
        controller.setSelectedNote(selectedNote);
        controller.getState().handleNote();
        controller.setDefaultNotesToView();
    }

    /**
     * Removes '%a' from provided note's title. Updates the view.
     * @param selectedNote provided note which is going to be modified.
     */
    public void returnFromArchive(Note selectedNote) {
        String title = selectedNote.getTitle().split("%a")[1];
        selectedNote.setTitle(title);
        controller.setSelectedNote(selectedNote);
        controller.getState().handleNote();
        controller.setArchivedNotesToView();
    }
}
