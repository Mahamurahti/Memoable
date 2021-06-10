package noteApp.model;

import javafx.stage.FileChooser;
import noteApp.controller.Controller;
import noteApp.model.note.Note;
import noteApp.model.savestate.SaveProperties;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.StyledDocument;

import java.io.*;
import java.util.prefs.Preferences;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;


/**
 * Filehandler-class is responsible for saving files to the user's hard drive as well as opening them.
 *
 * @author Teemu Viljanen
 * @author Nico Järvinen
 * @author Matias Vainio
 */
public class Filehandler {

    /**
     * User preferences
     */
    private final Preferences prefs;
    /**
     * Simple text to be saved
     */
    private String text;
    /**
     * Name of file to be saved or read
     */
    private String filename;
    /**
     * Savepath for filesaving
     */
    private String path;
    /**
     * Controller object to be used in saving operations
     */
    private Controller controller;

    /**
     * Creates a Filehandler object.
     * @param controller Controller of the application
     */
    public Filehandler(Controller controller) {
        this.prefs = Preferences.userRoot().node(this.getClass().getName());
        this.controller = controller;
    }

    public Filehandler() {
        this.prefs = Preferences.userRoot().node(this.getClass().getName());
    }

    /**
     * Saves a simple text-file on the user's hard drive.
     *
     * @param filename sets the name of the file that is to be saved
     * @param text     sets the text that is to be saved wihin the file
     */
    public void write(String filename, String text) {

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Retrieves a simple text-file from the user's hard drive.
     *
     * @param filename sets the name of the file that is to be retrieved
     * @return returns the text from the retrieved file
     */
    public String read(String filename) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * Compresses a simple text-file
     *
     * @param fileToCompress sets the file to be compressed
     * @param compressFile   sets the target file
     * @throws IOException invalid path
     */
    public void compressFile(String fileToCompress, String compressFile) throws IOException {
        try (
                FileInputStream fin = new FileInputStream(fileToCompress);
                FileOutputStream fout = new FileOutputStream(compressFile);
                DeflaterOutputStream dos = new DeflaterOutputStream(fout)) {
            int i;
            while ((i = fin.read()) != -1) {
                dos.write((byte) i);
                dos.flush();
            }
        }
    }

    /**
     * Decompresses a compressed simple text file
     *
     * @param fileToDeCompress sets the file to decompress
     * @param deCompressFile   sets the target file
     * @throws IOException invalid path
     */
    public void decompressFile(String fileToDeCompress, String deCompressFile) throws IOException {
        try (
                FileInputStream fin = new FileInputStream(fileToDeCompress);
                InflaterInputStream in = new InflaterInputStream(fin);
                FileOutputStream fout = new FileOutputStream(deCompressFile)) {
            int i;
            while ((i = in.read()) != -1) {
                fout.write((byte) i);
                fout.flush();
            }

        }

    }


    /**
     * Saves the note as a rich text file
     *
     * @param area text area from where the note is taken
     * @param note note to be saved
     * @param name name of file to be saved
     */
    public void encodeFile(InlineCssTextArea area, Note note, String name) {
        StyledDocument<String, String, String> doc = DocumentHandler.encode(
                area, note
        );
        // checks if user has set a default directory
        String initialDir = "";
        if (SaveProperties.getPath() != null) {
            initialDir = SaveProperties.getPath();
        }
        File selectedFile;
        if (name == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save document");
            fileChooser.setInitialDirectory(new File(initialDir));
            fileChooser.setInitialFileName(note.getTitle() + ".txt");
            selectedFile = fileChooser.showSaveDialog(null);
        } else {
            selectedFile = new File(note.getTitle());
        }
        if (selectedFile != null) {
            try {
                FileOutputStream fos = new FileOutputStream(selectedFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(note);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens a rich text file onto the text area
     *
     * @param area text area to where the file is loaded
     * @param name name of file to open
     */
    public void decodeFile(InlineCssTextArea area, String name) {
        //checks if user has set a default directory
        String initialDir = "";
        if (SaveProperties.getPath() != null) {
            initialDir = SaveProperties.getPath();
        }
        File selectedFile;
        if (name == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load document");
            fileChooser.setInitialDirectory(new File(initialDir));
            fileChooser.setSelectedExtensionFilter(
                    new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
            selectedFile = fileChooser.showOpenDialog(null);
        } else {
            selectedFile = new File(name);
        }
        if (selectedFile != null) {
            try {
                FileInputStream fis = new FileInputStream(selectedFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Note note = (Note) ois.readObject();
                DocumentHandler.decode(area, note.getContent());
                if (controller != null) {
                    controller.addToNotesList(note);
                }

                fis.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                controller.getMlc().fileFormatError();
            }
        }
    }

    /**
     * Basically same as {@link #decodeFile(InlineCssTextArea, String)}.
     * <p>
     * Opens a file from the filesystem and returns it to the caller as a Note.
     *
     * @param file {file} File to be opened.
     * @return Note object.
     */
    public Note decodeFiles(File file) {
        //checks if user has set a default directory
        if (file != null) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                return (Note) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                controller.getMlc().fileFormatError();
            }
        }
        return null;
    }

    /**
     * Removes a note file from the hard drive.
     * @param note note to be removed.
     */
    public void removeFile(Note note) {
        File f = new File(SaveProperties.getPath() + "/" + note.getTitle() + ".txt");
        f.delete();
    }
}