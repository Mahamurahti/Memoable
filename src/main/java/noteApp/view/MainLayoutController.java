package noteApp.view;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import noteApp.Main;
import noteApp.controller.Controller;
import noteApp.model.Context;
import noteApp.model.note.Note;
import noteApp.model.note.Tag;
import noteApp.model.savestate.OnlineState;
import noteApp.utils.FileArrange.Sorter;
import noteApp.utils.FileArrange.SortingType;
import noteApp.view.listCell.ContextMenuListCell;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;


import java.io.*;
import java.util.*;

import static noteApp.model.DocumentHandler.decode;
import static noteApp.model.DocumentHandler.encode;

/**
 * Controller for the main layout
 *
 * @author Eric Keränen
 * @author Matias Vainio
 * @author Jere Salmensaari
 * @author Teemu Viljanen
 * @author Nico Järvinen
 */
public class MainLayoutController {

    private final KeyControl keyControl = new KeyControl();
    private Note selectedNote = new Note();
    private Note saveableNote = new Note();
    private boolean isModified = false;
    private Main main;
    private RootLayoutController rootLayoutController;
    private Controller con;
    private List<Note> noteList = new ArrayList<>();
    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private SortingType sortingType = SortingType.NONE;
    private ObservableList<String> labels;
    private ChangeListener<Note> changeListener;
    private final FontStyle fs = new FontStyle();

    @FXML
    private Label title;
    @FXML
    private ComboBox<String> comboBoxSort;
    @FXML
    private ListView<Note> savedNoteView;
    @FXML
    private Button newButton;
    @FXML
    private AnchorPane rightAnchor;
    @FXML
    private TextField noteName;
    @FXML
    private ComboBox<String> comboBoxTag;
    @FXML
    private ComboBox<String> comboBoxLabel;
    @FXML
    private InlineCssTextArea note;
    @FXML
    private HBox toolBar;
    @FXML
    private Button archiveButton;
    @FXML
    private Button saveAsButton;
    @FXML
    private HBox plus;
    @FXML
    private ComboBox<String> fontButton;
    @FXML
    private HBox panel;
    @FXML
    private VBox vbox;
    @FXML
    private Label label;
    @FXML
    private Label priority;

    public MainLayoutController() {
    }

    /**
     * Initializes the main layout with information and rules.
     */
    @FXML
    private void initialize() {
        initFonts();

        limitTextFieldLength(100);

        limitTextFieldLength(32);

        initListener();
        initSortBox();
        initTagBox();
        initLabelBox();

        selectedNote = null;

        savedNoteView.setCellFactory(ContextMenuListCell.forListView(this));

        //rich text testing
        note.getStylesheets().add("stylesheets/manual-highlighting.css");
        note.setWrapText(true);
        VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(note);
        VBox.setVgrow(vsPane, Priority.ALWAYS);
        vbox.getChildren().addAll(vsPane);
        //rich test testing end

        disableProperties();
        note.setStyle(0, fs.toCss());
    }

    /**
     * Initializes the note lists listener
     */
    private void initListener() {
        changeListener = (observable, oldValue, newValue) -> {
            if (noteList.size() > 0) {
                if(con.getState().getClass().getSimpleName().equals("OnlineState"))
                    disableProperties();
                else
                    disableProps();
                setInfo(noteList.get(getIndex()));
            }
        };
    }

    public void startListening() {
        savedNoteView.getSelectionModel().selectedItemProperty().addListener(changeListener);
    }

    public void stopListening() {
        savedNoteView.getSelectionModel().selectedItemProperty().removeListener(changeListener);
    }

    /**
     * Sets the list view depending on the state
     *
     * @see noteApp.model.savestate.AbstractState
     */
    public void setList() {
        if (con.getState() == null) {
            con.setState(new OnlineState(con, this));
        }
        con.getState().setList();
    }

    /**
     * Sets and empty list to the view and clear everything from the text areas
     */
    public void setEmptyList() {
        savedNoteView.getItems().clear();
        noteList.clear();
        getNoteName().setText("");
        setNoteText("");
    }

    /**
     * Sets info to the view when a note is clicked.
     *
     * @param i {Note} is the index of the clicked note
     */
    private void setInfo(Note i) {
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", rootLayoutController.getLanguageLocale(rootLayoutController.getLanguage()));
        selectedNote = null;
        String compNoteId = i.getId().toString();

        for (Note value : noteList) {
            if (value.getId().toString().equals(compNoteId)) {
                selectedNote = value;
            }
        }

        if (selectedNote != null) {
            String name = selectedNote.getTitle();
            String text = selectedNote.getContent();
            String label = selectedNote.getLabel();
            Tag tag = selectedNote.getTag();

            note.clear();
            if (name.contains("%a")) {
                noteName.setText(name.split("%a")[1]);
            } else {
                noteName.setText(name);
            }

            decode(note, text);

            if (!(label.strip().equals(""))) {
                comboBoxLabel.getSelectionModel().select(label);
            } else {
                comboBoxLabel.getSelectionModel().clearSelection();
            }

            if (!tag.toString().equals("")) {
                comboBoxTag.getSelectionModel().select(String.valueOf(tag));
            } else {
                comboBoxTag.getSelectionModel().clearSelection();
            }

        } else {
            noteName.setPromptText(bundler.getString("myNote"));
        }
    }

    private int getIndex() {
        int ind = savedNoteView.getSelectionModel().getSelectedIndex();
        if (ind < 0) {
            ind = 0;
        }
        return ind;
    }

    /**
     * Calls a saving function from root, that opens a path chooser to save the file
     *
     * @param e ActionEvent
     * @throws IOException if saving to file fails
     */
    @FXML
    private void handleSaveAs(ActionEvent e) throws IOException {
        e.consume();
        saveFile();
    }

    /**
     * Creates a new note with placeholder title.
     *
     * @param e ActionEvent
     */
    @FXML
    private void handleNew(ActionEvent e) {
        e.consume();
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", rootLayoutController.getLanguageLocale(rootLayoutController.getLanguage()));
        note.clear();
        String content = encode(note, null).getText();
        note.replaceText(content);
        con.getState().createNote(bundler.getString("noteName"), "");
        con.getState().setList();
        selectNote(savedNoteView.getItems().size());
        // Don't know why these don't work...
        fs.resetStyling();
        note.setStyle(0, note.getLength(), fs.toCss());
    }

    /**
     * Opens a separate window, that wants the users confirmation about the deletion of the currently
     * selected file. File deletion is dependent on the state.
     *
     * @see noteApp.model.savestate.AbstractState
     * @param e ActionEvent
     */
    @FXML
    public void handleDelete(ActionEvent e) {
        e.consume();
        con.getState().handleDelete();
    }

    /**
     * Handles keypress on "bold" button. Calls format method with bold parameter and selection in the note
     * area.
     * @param e {ActionEvent} action event which fires when user clicks a button.
     */
    @FXML
    private void handleBold(ActionEvent e) {
        e.consume();
        IndexRange range = note.getSelection();
        format("bold", range);
    }

    /**
     * Handles keypress on "italic" button. Calls format method with bold parameter and selection in the note
     * area.
     * @param e {ActionEvent} action event which fires when user clicks a button.
     */
    @FXML
    private void handleItalic(ActionEvent e) {
        e.consume();
        IndexRange range = note.getSelection();
        format("italic", range);
    }

    /**
     * Handles keypress on "underline" button. Calls format method with bold parameter and selection in the note
     * area.
     * @param e {ActionEvent} action event which fires when user clicks a button.
     */
    @FXML
    private void handleUnderline(ActionEvent e) {
        e.consume();
        IndexRange range = note.getSelection();
        format("underline", range);
    }
    /**
     * Used in formatting note content. Checks if textarea already contains style in question. Otherwise applies
     * style in question.
     *
     * @param style {String} is the name of the style
     * @param range {IndexRange} is the index range of the style
     */
    private void format(String style, IndexRange range) {
        String styleStr = note.getStyleSpans(range).getStyleSpan(0).getStyle();
        fs.setFromStyleString(styleStr);

        switch (style) {
            case "bold":
                fs.setBold(!styleStr.contains("bold"));
                break;
            case "italic":
                fs.setCursive(!styleStr.contains("italic"));
                break;
            case "underline":
                fs.setUnderline(!styleStr.contains("-fx-underline: true;"));
                break;
        }
        note.setStyle(range.getStart(), range.getEnd(), fs.toCss());
        note.requestFocus();
        con.getState().handleNote();
    }

    /**
     * Cycles font size for currently selected area. There are three different font sizes. Sizes are defined in
     * {@link noteApp.view.FontStyle}.
     *
     * Notifies auto save thread that there is modified content.
     * @param e {ActionEvent} event which fires on user clicking a button.
     */
    @FXML
    private void handleFontSize(ActionEvent e) {
        e.consume();
        int fontSize = fs.getFontSize();
        IndexRange range = note.getSelection();

        String styleStr = note.getStyleSpans(range).getStyleSpan(0).getStyle();
        fs.setFromStyleString(styleStr);

        switch (fontSize) {
            case FontStyle.FONT_SMALL:
                fs.setFontSize(FontStyle.FONT_MED);
                note.setStyle(range.getStart(), range.getEnd(), fs.toCss());
                break;
            case FontStyle.FONT_MED:
                fs.setFontSize(FontStyle.FONT_LARGE);
                note.setStyle(range.getStart(), range.getEnd(), fs.toCss());
                break;
            case FontStyle.FONT_LARGE:
                fs.setFontSize(FontStyle.FONT_SMALL);
                note.setStyle(range.getStart(), range.getEnd(), fs.toCss());
                break;
        }

        con.getState().handleNote();
    }

    /**
     * Opens a file to the application.
     */
    public void getFile() {
        con.decodeFile(note);
    }

    /**
     * Saves a file to the computer.
     */
    public void saveFile() {
        saveableNote.setTitle(noteName.getText());
        con.encodeFile(note, saveableNote);
    }

    /**
     * Changes font based on user selection.
     * @param e {ActionEvent} fires when user selects an item from a list.
     */
    @FXML
    private void handleFont(ActionEvent e) {
        e.consume();
        fs.setFont(fontButton.getSelectionModel().getSelectedItem());
        note.setStyle(0, note.getLength(), fs.toCss());
        con.getState().handleNote();
    }

    /**
     * Initializes fonts currently installed on system to a list.
     */
    private void initFonts() {
        List<String> fonts = Font.getFamilies();
        fontButton.getItems().addAll(fonts);
    }

    /**
     * Handles what to do with the note after a change is made. If the state is online, activates autosaving after a
     * certain period of time and if the state is offline, doesn't activate the autosaving.
     *
     * @param e keyevent, does nothing
     * @see noteApp.model.savestate.OnlineState
     * @see noteApp.model.savestate.OfflineState
     */
    @FXML
    private void handleNote(KeyEvent e) {
        selectedNote.setTitle(noteName.getText());
        selectedNote.setContent(note.getText());
        con.getState().handleNote();
    }

    /**
     * Returns isModified variable.
     *
     * @return isModified {boolean}
     * @throws InterruptedException if thread is interrupted
     */
    public synchronized boolean getIsModified() throws InterruptedException {
        return isModified;
    }

    /**
     * Modifies state of isModified variable based on if user is typing a new note.
     *
     * @param mod value to be set.
     * @throws InterruptedException if thread is interrupted
     */
    public synchronized void setIsModified(boolean mod) throws InterruptedException {
        this.isModified = mod;
        while (!isModified) wait();
        notifyAll();
    }

    /**
     * Clears note selection and disables properties
     */
    public void clearDisableWrapper() {
        clearNoteSelection();
        disableProperties();
    }

    /**
     * Selects a note. Called after an action is made.
     *
     * @param index {int} of the note to select
     */
    public void selectNote(int index) {
        Platform.runLater(() -> {
            if (savedNoteView.getItems().size() > 0) {
                savedNoteView.getSelectionModel().select(index);
                noteName.positionCaret(noteName.getText().length());
            }
        });
    }

    /**
     * Clears list selection and displays the template view
     */
    public void clearNoteSelection() {
        Platform.runLater(() -> {
            savedNoteView.getSelectionModel().clearSelection();
            selectedNote = null;
            noteName.setText("");
            note.replaceText("");
        });
    }

    /**
     * Disables properties in the main layout, that cannot be accessed at the time.
     * Called if the state is online
     *
     * @see noteApp.model.savestate.OnlineState
     */
    public void disableProperties() {
        Platform.runLater(() -> {
            rightAnchor.setDisable(selectedNote == null);
            comboBoxSort.setDisable(savedNoteView.getItems().size() == 0);
            newButton.setDisable(!this.con.isLoggedIn());
            note.setDisable(selectedNote == null);

            rootLayoutController.getEditMenu().setDisable(selectedNote == null);
            rootLayoutController.getFileMenu(0).setDisable(selectedNote == null);
            rootLayoutController.getFileMenu(1).setDisable(con.getState() == null);
            rootLayoutController.getEditMenu().setDisable(selectedNote == null);
            rootLayoutController.getChangeViewMenu().setDisable(this.con.isLoggedIn());
        });
    }

    /**
     * Disables properties in the main layout, that cannot be accessed at the time.
     * Called if the state is offline
     *
     * @see noteApp.model.savestate.OfflineState
     */
    public void disableProps() {
        Platform.runLater(() -> {
            rightAnchor.setDisable(selectedNote == null);
            comboBoxSort.setDisable(savedNoteView.getItems().size() == 0);
            newButton.setDisable(false);
            note.setDisable(selectedNote == null);

            rootLayoutController.getEditMenu().setDisable(selectedNote == null);
            rootLayoutController.getFileMenu(0).setDisable(selectedNote == null);
            rootLayoutController.getFileMenu(1).setDisable(con.getState() == null);
            rootLayoutController.getEditMenu().setDisable(selectedNote == null);
            rootLayoutController.getChangeViewMenu().setDisable(true);
            archiveButton.setDisable(true);
        });
    }

    /**
     * Disables list of notes. Prevents user changing notes when saving is underway.
     * @param b {boolean} boolean value to be passed to setDisable. True when disabling is wanted. Else otherwise.
     */
    public void disableList(boolean b) {
        Platform.runLater(() -> {
            savedNoteView.setDisable(b);
        });
    }

    /**
     * Initializes the sorting combobox with sorting options and adds actions to the options.
     */
    private void initSortBox() {
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        comboBoxSort.getItems().add(bundler.getString("none"));
        comboBoxSort.getItems().add(bundler.getString("dDesc"));
        comboBoxSort.getItems().add(bundler.getString("dAsce"));
        comboBoxSort.getItems().add(bundler.getString("sortName"));
        comboBoxSort.getItems().add(bundler.getString("sortTag"));

        comboBoxSort.setOnAction(a -> {
            int index = comboBoxSort.getSelectionModel().getSelectedIndex();
            Object selected = comboBoxSort.getSelectionModel().getSelectedItem();

            if (index == 0) {
                sortingType = SortingType.NONE;
                con.getState().setList();
            } else if (index == 1) {
                sortingType = SortingType.DESCENDINGDATE;
                con.getState().setList();
            } else if (index == 2) {
                sortingType = SortingType.ASCENDINGDATE;
                con.getState().setList();
            } else if (index == 3) {
                sortingType = SortingType.DESCENDINGNAME;
                con.getState().setList();
            } else if (index == 4) {
                sortingType = SortingType.DESCENDINGTAG;
                con.getState().setList();
            }
        });
    }

    /**
     * Initializes the tag combobox with tags and adds actions to the options.
     */
    private void initTagBox() {
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        comboBoxTag.getItems().add(bundler.getString("bNone"));
        comboBoxTag.getItems().add(bundler.getString("high"));
        comboBoxTag.getItems().add(bundler.getString("medium"));
        comboBoxTag.getItems().add(bundler.getString("low"));

        comboBoxTag.setOnAction(a -> {
            int index = comboBoxTag.getSelectionModel().getSelectedIndex();
            Object selected = comboBoxTag.getSelectionModel().getSelectedItem();

            if (selected != null) {
                List<String> combolabels = comboBoxTag.getItems();
                if (!(combolabels.contains(selected.toString()) &&
                        !(selected.toString().strip().equals("")))) {
                    comboBoxTag.getItems().add(selected.toString());
                }
            }

            if (index == 0) {
                selectedNote.setTag(Tag.NONE);
            } else if (index == 1) {
                selectedNote.setTag(Tag.HIGH);
            } else if (index == 2) {
                selectedNote.setTag(Tag.MEDIUM);
            } else if (index == 3) {
                selectedNote.setTag(Tag.LOW);
            }
        });
    }

    /**
     * Initializes the label combobox with labels and adds actions to the options.
     */
    private void initLabelBox() {
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        labels = FXCollections.observableArrayList();
        labels.add(bundler.getString("sports"));
        labels.add(bundler.getString("school"));
        labels.add(bundler.getString("work"));

        comboBoxLabel.setItems(labels);
        comboBoxLabel.setOnAction(a -> {
            int index = comboBoxLabel.getSelectionModel().getSelectedIndex();
            Object selected = comboBoxLabel.getSelectionModel().getSelectedItem();

            if (selected != null) {
                List<String> combolabels = comboBoxLabel.getItems();
                if (!(combolabels.contains(selected.toString()) &&
                        !(selected.toString().strip().equals("")))) {
                    comboBoxLabel.getItems().add(selected.toString());
                }
                selectedNote.setLabel(selected.toString());
            }
        });
    }

    /**
     * Limits the note names length to a certain number of characters.
     *
     * @param limit {int} of the number of characters the name can contain
     */
    private void limitTextFieldLength(int limit) {
        noteName.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    if (noteName.getText().length() >= limit) {
                        noteName.setText(noteName.getText().substring(0, limit));
                    }
                }
            }
        });
    }

    /**
     * Sorts the note list by name
     */
    public void sortNotesByName() {
        notes = FXCollections.observableArrayList();
        for (int x = 0; x < noteList.size(); x++) {
            notes.add(noteList.get(x));
        }
        Collections.sort(notes, (object1, object2) -> object1.getTitle().compareToIgnoreCase(object2.getTitle()));

        savedNoteView.setItems(notes);
        savedNoteView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> setInfo(notes.get(getIndex())));
    }

    /**
     * Sorts the note list by date (descending)
     */
    public void sortNotesByDescDate() {
        notes = Sorter.sortDateDescending(noteList);
        savedNoteView.setItems(notes);
        savedNoteView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    setInfo(notes.get(getIndex()));
                });
    }

    /**
     * Sorts the note list by date (ascending)
     */
    public void sortNotesByAscDate() {
        notes = Sorter.sortDateAscending(noteList);
        savedNoteView.setItems(notes);
        savedNoteView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> setInfo(notes.get(getIndex())));
    }

    /**
     * Sorts the notes by tag (priority)
     */
    public void sortNotesByDescTag() {
        notes = Sorter.sortTagsDescending(noteList);
        savedNoteView.setItems(notes);
        savedNoteView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> setInfo(notes.get(getIndex())));
    }

    /**
     * Shows archived notes on the screen and change buttons.
     */
    public void setArchivedNotesToView() {
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        title.setText(bundler.getString("archive"));
        archiveButton.setText(bundler.getString("unArchive"));
        archiveButton.setOnAction((e) -> handleUnarchive());
        con.getState().setArchiveList();
        selectNote(0);
    }

    /**
     * Shows default notes on the screen and change buttons.
     */
    public void setDefaultNotesToView() {
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", Context.getInstance().getCurrentLocale());
        title.setText(bundler.getString("yourNotes"));
        archiveButton.setText(bundler.getString("archive"));
        archiveButton.setOnAction((e) -> handleArchive());
        con.getState().setList();
        selectNote(0);
    }

    /**
     * Archives a note and sends it to another collections in the database.
     */
    public void handleArchive() {
        try {
            con.archiveNote(selectedNote);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unarchives a note and sends it to the base collections in the database.
     */
    public void handleUnarchive() {
        this.con.unarchiveNote(this.selectedNote);
    }


    // Setters and getters:

    public String getNoteText() {
        return note.getText();
    }

    public void setNoteText(String text) {
        note.replaceText(text);
    }

    public TextField getNoteName() {
        return noteName;
    }

    public void setNoteName(TextField noteName) {
        this.noteName = noteName;
    }

    public String getNoteLabel() {
        return comboBoxLabel.getSelectionModel().getSelectedItem();
    }

    public Note getSelectedNote() {
        return selectedNote;
    }

    public void setSelectedNote(Note note) {
        this.selectedNote = note;
    }

    public void setCon(Controller con) {
        this.con = con;
    }

    public Main getMain() {
        return this.main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Note getSaveableNote() {
        return saveableNote;
    }

    public void setSaveableNote(Note note) {
        this.saveableNote = note;
    }

    public RootLayoutController getRootLayoutController() {
        return rootLayoutController;
    }

    public void setRootLayoutController(RootLayoutController root) {
        this.rootLayoutController = root;
    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ObservableList<Note> notes) {
        this.notes = notes;
    }

    public Label getTitle() {
        return title;
    }

    public void setTitle(Label title) {
        this.title = title;
    }

    public ComboBox<String> getComboBoxLabel() {
        return comboBoxLabel;
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Note> list) {
        this.noteList = list;
    }

    public ListView<Note> getSavedNoteView() {
        return this.savedNoteView;
    }

    public InlineCssTextArea getNoteArea() {
        return note;
    }

    public void setNoteArea(InlineCssTextArea area) {
        this.note = area;
    }

    public SortingType getSortingType() {
        return this.sortingType;
    }

    // End of setters and getters :)

    /**
     * Shows an error if the user tries to add a file that is not supported
     */
    public void fileFormatError(){
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", rootLayoutController.getLanguageLocale(rootLayoutController.getLanguage()));
        new Toast().showToast(getMain().getPrimaryStage(),
                bundler.getString("errorToast"), Color.RED, 3500, 500, 500);
    }

    /**
     * Sets the plus sign for the "Add Note" button
     */
    public void setPlus() {
        Text plusIcon = GlyphsDude.createIcon(FontAwesomeIcon.PLUS, "30px");
        plusIcon.getStyleClass().add("plus");
        plus.getChildren().add(plusIcon);
    }

    public void changeText() {
        ResourceBundle bundler = ResourceBundle.getBundle("bundles.MyBundle", rootLayoutController.getLanguageLocale(rootLayoutController.getLanguage()));
        title.setText(bundler.getString("yourNotes"));
        //noteName.setText(bundler.getString("noteName"));
        archiveButton.setText(bundler.getString("archives"));
        saveAsButton.setText(bundler.getString("saveas"));
        priority.setText(bundler.getString("prio"));
        label.setText(bundler.getString("label"));
        comboBoxSort.setPromptText(bundler.getString("sortby"));
        comboBoxLabel.setPromptText(bundler.getString("none"));
        comboBoxTag.setPromptText(bundler.getString("bNone"));
        fontButton.setPromptText(bundler.getString("font"));
        comboBoxSort.getItems().clear();
        comboBoxSort.getItems().add(bundler.getString("none"));
        comboBoxSort.getItems().add(bundler.getString("dDesc"));
        comboBoxSort.getItems().add(bundler.getString("dAsce"));
        comboBoxSort.getItems().add(bundler.getString("sortName"));
        comboBoxSort.getItems().add(bundler.getString("sortTag"));
        labels.clear();
        labels.add(bundler.getString("sports"));
        labels.add(bundler.getString("school"));
        labels.add(bundler.getString("work"));
        comboBoxTag.getItems().clear();
        comboBoxTag.getItems().add(bundler.getString("bNone"));
        comboBoxTag.getItems().add(bundler.getString("high"));
        comboBoxTag.getItems().add(bundler.getString("medium"));
        comboBoxTag.getItems().add(bundler.getString("low"));
    }
}
