package noteApp.model.backend;

import noteApp.model.Constants;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;


/**
 * Singleton class for handling connections to the backend of the application
 * @author Jere Salmensaari
 */
public class UserConnection {
	/**
	 * Instance of the class
	 */
	private static UserConnection INSTANCE;

	/**
	 * HttpClient for communicating with the backend
	 */
	private HttpClient client;

	/**
	 * Constructor, builds a new HttpClient
	 */
	private UserConnection() {
		client = HttpClient.newBuilder()
			.build();
	}

	/**
	 * Returns the instance of the class
	 * @return instance of the class
	 */
	public static UserConnection getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UserConnection();
		}
		return INSTANCE;
	}

	/**
	 * Pings the database to see if it can connect
	 * @return response from the database
	 * @throws IOException if sent request is incorrect
     * @throws InterruptedException If connection is interrupted
	 */
	public String ping() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(Constants.BASEURL+"/ping"))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200)
			return null;

		return response.body();

	}

	/**
	 * Logs the user in,
	 * <p>
	 * Sends a Http POST request to the backend to log the user in
	 * If login is succesfull returns the user's id and token
	 * @param username username to log in with
	 * @param password password to log in with
	 * @return user id and token as String
	 * @throws IOException if sent request is incorrect
	 * @throws InterruptedException if connection is interrupted
	 */
	public String login(String username, String password) throws IOException, InterruptedException {
		String body = "{ \"username\":\""+username+"\",\"password\":\""+password+"\"}";
		HttpRequest request = HttpRequest.newBuilder()
				.POST(BodyPublishers.ofString(body))
				.uri(URI.create(Constants.BASEURL+"/api/login"))
				.setHeader("Content-Type", "application/json")
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200)
			return null;
		return response.body();
	}

	/**
	 * Creates a new user.
	 * <p>
	 * Sends a Http POST request to the backend to create a new user.
	 * Returns user's id if succesfully created. If username is in use
	 * returns "unameErr"
	 * @param username username to create
	 * @param password password to create
	 * @return user id
	 * @throws IOException if sent request is incorrect
	 * @throws InterruptedException if connection is interrupted
	 */
	public String createUser(String username, String password) throws IOException, InterruptedException {
		String body = "{ \"username\":\""+username+"\",\"password\":\""+password+"\"}";
		
		HttpRequest request = HttpRequest.newBuilder()
				.POST(BodyPublishers.ofString(body))
				.uri(URI.create(Constants.BASEURL+"/api/user"))
				.setHeader("Content-Type", "application/json")
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200)
			return null;

		return response.body();

	}

	/**
	 * Deletes a user from the database.
	 * <p>
	 * Requires the uiId of the user to be deleted and a valid token
	 * for the same user.
	 * @param uId id of the user to be deleted
	 * @param token token of the user to be deleted
	 * @return true if succesfull
	 * @throws IOException if sent request is incorrect
	 * @throws InterruptedException if connection is interrupted
	 */
	public Boolean deleteUser(String uId, String token) throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder()
				.DELETE()
				.uri(URI.create(Constants.BASEURL+"/api/user/delete/"+uId))
				.setHeader("Authorization", "Bearer "+token)
				.build();

		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200)
			return false;
		return Boolean.valueOf(response.body());
	}

}
