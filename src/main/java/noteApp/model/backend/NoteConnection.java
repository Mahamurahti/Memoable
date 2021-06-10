package noteApp.model.backend;

import noteApp.model.Constants;
import noteApp.model.note.Note;
import noteApp.utils.note.NoteUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

/**
 * Singleton class for sending handling Note http requests
 * @author Jere Salmensaari
 */
public class NoteConnection {
    /**
     * Instance of the class
     */
    private static NoteConnection INSTANCE;

    /**
     * HttpClient for communicating with the backend
     */
    private HttpClient client;

    /**
     * Constructor, creates a new HttpClient
     */
    private NoteConnection() {
        client = HttpClient.newBuilder().build();
    }

    /**
     * Returns the instance of the class
     * @return instance of the class
     */
    public static NoteConnection getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoteConnection();
        }

        return INSTANCE;
    }

    /**
     * Gets a list of Notes belonging to a given user
     * <p>
     * Sends a Http GET request to the backend and parses the returned
     * JSON String into a list of Notes
     * @param uId user id
     * @param token token of the user
     * @return List of Notes
     * @throws IOException if sent request is incorrect
     * @throws InterruptedException If connection is interrupted
     */
    public List<Note> getUserNotes(String uId, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(Constants.BASEURL + "/api/notes/" + uId))
                .setHeader("Authorization", "Bearer " + token)
                .setHeader("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() != 200 || response.body().equals(""))
            return null;

        List<Note> notes = NoteUtil.parseList(response.body());
        return notes;
    }

    /**
     * Saves a note to the database
     * <p>
     * Sends a Http POST request to the backed, parses the
     * returned JSON string into a note.
     * @param note Note to save
     * @param uId user id
     * @param token token of the user
     * @return saved Note
     * @throws IOException if sent request is incorrect
     * @throws InterruptedException if connection is interrupted
     */
    public Note saveNote(Note note, String uId, String token) throws IOException, InterruptedException {
        String body = NoteUtil.buildJsonNote(note);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(Constants.BASEURL + "/api/notes/save/" + uId))
                .setHeader("Authorization", "Bearer " + token)
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        if (response.statusCode() != 200 || response.body().equals(""))
            return null;

        return NoteUtil.parse(response.body());
    }

    /**
     * Deletes a note with the given id.
     * <p>
     * Sends a Http DELETE request to the backend
     * @param noteId note to delete
     * @param uId user id
     * @param token token of the user
     * @return true if succesfully deleted, otherwise false
     * @throws IOException if sent request is incorrect
     * @throws InterruptedException if connection is interrupted
     */
    public Boolean deleteNote(String noteId, String uId, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(Constants.BASEURL + "/api/notes/delete/" + uId + "/" + noteId))
                .setHeader("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() != 200)
            return false;
        return Boolean.valueOf(response.body());

    }

}
