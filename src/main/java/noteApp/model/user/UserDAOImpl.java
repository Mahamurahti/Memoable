package noteApp.model.user;

import noteApp.Main;
import noteApp.controller.Controller;
import noteApp.model.backend.UserConnection;
import noteApp.model.note.Exceptions.InvalidUserException;
import noteApp.view.MainLayoutController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

/**
 * User Data Access Object. Used when accessing user data in database.
 *
 * @author Matias Vainio
 */
public class UserDAOImpl implements UserDAO {

    /**
     * Listener checking if the user is logged in
     */
    protected PropertyChangeSupport propertyChangeSupport;
    private String userString;

    /**
     * Constructor, initializes the propertyChangeSypport with
     * mainlayoutController and controller
     * @param mainLayoutController mlc of the application
     * @param controller controller of the application
     */
    public UserDAOImpl(MainLayoutController mainLayoutController, Controller controller) {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        SecurityUtils.setSecurityManager(securityManager);
        this.propertyChangeSupport = new PropertyChangeSupport(this);

        addPropertyChangeListener(new UserListener(mainLayoutController, controller));
    }

    /**
     * Empty constructor, used for testing
     */
    public UserDAOImpl() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        SecurityUtils.setSecurityManager(securityManager);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Checks if user with the same username exists. If not Inserts a new User object to database.
     * Hashes password and stores it to the user object.
     *
     * @param username username to create user with
     * @param password password to create user with
     */
    @Override
    public boolean create(String username, String password) throws InvalidUserException, IOException, InterruptedException {
        if (username.equals("") || password.equals("")) {
            throw new InvalidUserException("Username or password field is empty");
        }

        String hashedPw = new Sha256Hash(password, "", Main.HASH_ITERATIONS).toHex();
        String response = UserConnection.getInstance().createUser(username, hashedPw);
        if (response.equals("unameErr")) {
            throw new InvalidUserException("Username already in use");
        }
        return true;
    }


    /**
     * Logs user in using shiro. User credentials are given as parameters.
     *
     * @param username username given by user.
     * @param password password given by user.
     * @return true if log in is successful. False otherwise.
     */
    @Override
    public boolean logIn(String username, String password) throws UnknownAccountException, IncorrectCredentialsException {
        String hashedPw = new Sha256Hash(password, "", Main.HASH_ITERATIONS).toHex();

        try {
            String response = UserConnection.getInstance().login(username, hashedPw);
            if (response == null)
                throw new UnknownAccountException();
            Subject currentUser = SecurityUtils.getSubject();
            String[] respPieces = response.split(" ");
            currentUser.getSession().setAttribute("id", respPieces[0]);
            currentUser.getSession().setAttribute("token", respPieces[1]);
        } catch (IOException | InterruptedException e) {
            return false;
        }
        setCurrentUser(username);
        return true;
    }

    /**
     * Sets current user to null. Uses shiro to remove credentials from memory. Fires LoggedOut event.
     */
    @Override
    public boolean logOut() {
        this.userString = null;
        SecurityUtils.getSubject().logout();
        propertyChangeSupport.firePropertyChange("LoggedOut", "foo", "bar");
        return true;
    }

    /**
     * Sets current user's username as userString variable which is then used to observe changes in user status.
     *
     * @param username username to be set as current user.
     */
    public void setCurrentUser(String username) {
        String oldUser = this.userString;
        this.userString = username;
        propertyChangeSupport.firePropertyChange("LoggedIn", oldUser, userString);
    }

    /**
     * Check if user is authenticated.
     *
     * @return true if user is authenticated.
     */
    public boolean userAuthenticated() {
        Subject currentUser = SecurityUtils.getSubject();
        return currentUser.isAuthenticated();
    }

    /**
     * Adds listener which is used to observe changes that occur in userString.
     *
     * @param listener listener to be added.
     */
    private void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Creates a salt which is used to hash passwords.
     *
     * @return ByteSource salt in ByteSource form.
     */
    protected ByteSource getSalt() {
        return new SecureRandomNumberGenerator().nextBytes();
    }

}

