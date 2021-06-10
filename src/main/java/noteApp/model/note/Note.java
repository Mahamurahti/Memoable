package noteApp.model.note;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Note class for storing details of created notes.
 * @author Matias Vainio
 * @author Jere Salmensaari
 */
public class Note implements Serializable {
    private ObjectId id;
    private String userId;
    private String title;
    private String content;
    private Date date;
    private Tag tag;
    private String label;

    /**
     * Creates note with null content and title
     */
    public Note() {
        this.id = new ObjectId();
        this.title = null;
        this.userId = null;
        this.content = null;
        this.date = new Date();
        this.tag = Tag.NONE;
        this.label = "";
    }

    /**
     * Creates Note with title and contet
     * @param title title to set to the Note
     * @param content content to set to the Note
     */
    public Note(String title, String content) {
        this.id = new ObjectId();
        this.title = title;
        this.userId = "";
        this.content = content;
        this.date = new Date();
        this.tag = Tag.NONE;
        this.label = "";
    }

    /**
     * Returns User Id of the note
     * @return User id of the note
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the User Id of the note
     * @param userId User id to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the ObjectId of the Note
     * @return ObjectId of the note
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * Sets the ObjectId of the Note
     * @param id ObjectId to be set
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * Sets the ObjectId of the Note in String form
     * @param id String id to be set into Objectid
     */
    public void setId(String id) {
        this.id = new ObjectId(id);
    }

    /**
     * Returns the content of the Note
     * @return Content of the Note
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the Content of the note
     * @param content content to be set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the date the Note was last modified on
     * @return Date the Note was last modified on
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date the Note was last modified on
     * @param date Date the Note was last modified on
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Retrurns the Date the Note was last modified on as a String
     * @return Date the Note was last modified on as a String
     * @throws ParseException if Date cannot be parsed
     */
    public String getDateString() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        return sdf.format(this.date);
    }

    /**
     * Returns the localized version of the Date the Note was last modified on
     * @param lang language to localize to
     * @return Localized String representation of the Date the Note was last modified on
     * @throws ParseException if Date cannot be parsed
     */
    public String getLocalizedDateString(String lang) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        switch (lang) {
            case "en":
                sdf = new SimpleDateFormat("hh:mm MM/dd/yyyy");
                break;
            case "es":
                sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                break;
        }

        return sdf.format(this.date);
    }

    /**
     * Returns the title of the Note
     * @return title of Note
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the title of the Note
     * @param title title of the Note
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the Tag of the Note
     * @return Tag of the note
     */
    public Tag getTag() {
        return this.tag;
    }

    /**
     * Sets the Tag of the Note
     * @param tag Tag to be set
     */
    public void setTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * Returns the label of the Note
     * @return label of the Note
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets the label of the Note
     * @param label label to be set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns a String representation of the Note
     */
    @Override
    public String toString() {
        if (getTitle().contains("%a")) {
            return getTitle().split("%a")[1];
        }
        return getTitle();
    }
}
