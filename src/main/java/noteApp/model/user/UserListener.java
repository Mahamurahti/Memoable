package noteApp.model.user;

import noteApp.controller.Controller;
import noteApp.model.savestate.OfflineState;
import noteApp.model.savestate.OnlineState;
import noteApp.view.MainLayoutController;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Listens for change in current user and tells view to update accordingly.
 *
 * @author Matias Vainio
 */
public class UserListener implements PropertyChangeListener {
    private final MainLayoutController mainLayoutController;
    private final Controller controller;

    /**
     * Constructor for the class. Takes {@link MainLayoutController} and {@link Controller} as parameters.
     *
     * @param mainLayoutController reference to MainLayoutController.
     * @param controller reference to Controller.
     */
    public UserListener(MainLayoutController mainLayoutController, Controller controller) {
        this.mainLayoutController = mainLayoutController;
        this.controller = controller;
    }

    /**
     * Fired event when property changes. When user logs in this fires LoggedIn event.
     * When user logs out fires LoggedOut event.
     *
     * @param propertyChangeEvent PropertyChangeEvent which happens when user logs in or out of the app.
     */
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("LoggedIn")) {
            controller.setState(new OnlineState(controller, mainLayoutController));
            controller.getState().setList();
        } else if (propertyChangeEvent.getPropertyName().equals("LoggedOut")) {
            controller.setEmptyList();
            controller.setState(new OfflineState(mainLayoutController));
        }
    }
}
