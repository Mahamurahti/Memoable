package noteApp.utils.note;

import noteApp.model.note.Note;

import noteApp.model.note.Tag;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class to convert JSON to Note objects and Note objects to JSON
 * @author Jere Salmensaari
 */
public class NoteUtil {

    /**
     * Takes a JSON object and parses it out to a list of Notes
     * <p>
     * If the JSON object is somehow incorrectly formatted throws JSONException
     * @param json String representation of a list of Notes as JSON
     * @return list of Note-objects
     */
    public static List<Note> parseList(String json) {
        List<Note> notes = new ArrayList<>();

        JSONArray arr = new JSONArray(json);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jo = arr.getJSONObject(i);
            notes.add(parse(jo.toString()));
        }

        return notes;
    }

    /**
     * Takes a single Note in JSON form and parses it out to a Note-object
     * <p>
     * If the JSON object is somehow incorrectly formatted throws JSONException
     * @param json String representation of a Ntoe as JSON
     * @return Note-object
     */
    public static Note parse(String json) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        JSONObject jo = new JSONObject(json);
        Note temp = new Note();
        temp.setTitle((String)jo.get("title"));
        temp.setContent((String)jo.get("content"));
         
        try {
            temp.setId((String)jo.get("noteId"));
        } catch (Exception e) {
            temp.setId(new ObjectId());
        }

        Tag tag = Tag.NONE;
        try {
            tag = Tag.valueOf((String) jo.get("tag"));
            temp.setDate(sdf.parse((String)jo.get("dateString")));
        } catch (IllegalArgumentException e) {
            System.out.println("No tag found for value "+ (String)jo.get("tag"));
        } catch (JSONException | ParseException e) {
            System.out.println("Could not parse date");
            temp.setDate(new Date());
        }
        temp.setTag(tag);
        temp.setLabel((String)jo.get("label"));
        return temp;
    }

    /**
     * Takes a Note-object and builds a JSON String out of it
     * @param note Note-object to parse
     * @return String representation of the Note-object as JSON
     */
    public static String buildJsonNote(Note note) {
        JSONObject obj = new JSONObject();
        obj.put("id", note.getId());
        obj.put("title", note.getTitle());
        obj.put("content", note.getContent());
        obj.put("label", note.getLabel());
        obj.put("tag", note.getTag().toString());
        try {
            obj.put("date", note.getDateString());
        } catch (ParseException e) {
            obj.put("date", "none");
        }
        return obj.toString();
    }
}
