package noteApp.model.user;

/**
 * Class for connecting into the database for user related functions
 */
public interface UserDAO {
    /**
     * Logs the user in,
     * @param username username to log in with
     * @param password password to log in with
     * @return true if succesfully logged in
     */
    boolean logIn(String username, String password);

    /**
     * Creates a user
     * @param username username to create
     * @param password password to create
     * @return true if succesfully created user
     * @throws Exception if username is already in user
     */
    boolean create(String username, String password) throws Exception;

    /**
     * Checks if user is logged in
     * @return true if logged in
     */
    boolean userAuthenticated();

    /**
     * Logs the user our
     * @return true if succesfully logged out
     */
    boolean logOut();

}
