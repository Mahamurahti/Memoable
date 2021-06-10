package noteApp.utils.FileArrange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import noteApp.model.note.Note;
import noteApp.model.note.Tag;

/**
 * Sorts notes in order of Tags of Labels
 * @author Jere Salmensaari
 */
public class Sorter {

    /**
     * Sorts Notes in order of Tags descending
     * @param notes notes to sort
     */
    public static void sortTagsDescending(Note[] notes) {
        quickSortNotes(notes, 0, notes.length-1, SortingType.DESCENDINGTAG);
    }

    /**
     * Sorts Notes in order of Tags ascending
     * @param notes notes to sort
     */
    public static void sortTagsAscending(Note[] notes) {
        quickSortNotes(notes, 0, notes.length-1, SortingType.ASCENDINGTAG);
    }

    /**
     * Sorts Notes in order of Tags descending
     * @param notes List of Note-objects
     * @return ObservableList of Note-objects
     */
    public static ObservableList<Note> sortTagsDescending(List<Note> notes) {
        Note[] noteArr = notes.toArray(new Note[0]);
        quickSortNotes(noteArr, 0, noteArr.length-1, SortingType.DESCENDINGTAG);
        ObservableList<Note> noteList = FXCollections.observableArrayList();
        for (Note val : noteArr) {
            noteList.add(val);
        }
        return noteList;
    }

    /**
     * Sorts Notes in order of Tags ascending
     * @param notes List of Note-objects
     * @return ObservableList of Note-objects
     */
    public static ObservableList<Note> sortTagsAscending(List<Note> notes) {
        Note[] noteArr = notes.toArray(new Note[0]);
        quickSortNotes(noteArr, 0, noteArr.length-1, SortingType.ASCENDINGTAG);
        ObservableList<Note> noteList = FXCollections.observableArrayList();
        for (Note val : noteArr) {
            noteList.add(val);
        }
        return noteList;
    }

    /**
     * Sorts Notes in order of Date ascending
     * @param notes notes to sort
     */
    public static void sortDateAscending(Note[] notes) {
        quickSortNotes(notes, 0, notes.length-1, SortingType.ASCENDINGDATE);
    }

    /**
     * Sorts Notes in order of Date ascending
     * @param notes List of Note-objects
     * @return ObservableList of Note-objects
     */
    public static ObservableList<Note> sortDateAscending(List<Note> notes) {
        Note[] noteArr = notes.toArray(new Note[0]);
        quickSortNotes(noteArr, 0, noteArr.length-1, SortingType.ASCENDINGDATE);
        ObservableList<Note> noteList = FXCollections.observableArrayList();
        for (Note val : noteArr) {
            noteList.add(val);
        }
        return noteList;
    }

    /**
     * Sorts Notes in order of Date descending
     * @param notes notes to sort
     */
    public static void sortDateDescending(Note[] notes) {
        quickSortNotes(notes, 0, notes.length, SortingType.DESCENDINGDATE);
    }

    /**
     * Sorts Notes in order of Date descending
     * @param notes List of Note-objects
     * @return ObservableList of Note-objects
     */
    public static ObservableList<Note> sortDateDescending(List<Note> notes) {
        Note[] noteArr = notes.toArray(new Note[0]);
        quickSortNotes(noteArr, 0, noteArr.length-1, SortingType.DESCENDINGDATE);
        ObservableList<Note> noteList = FXCollections.observableArrayList();
        for (Note val : noteArr) {
            noteList.add(val);
        }
        return noteList;
    }

    /**
     * Sorts notes using the quicksort-algorighm
     * @param A Array to sort
     * @param lo low index
     * @param hi high index
     * @param type sorting type
     */
    private static void quickSortNotes(Note[] A, int lo, int hi, SortingType type) {
        if (lo < hi) {
            int p = partition(A, lo, hi, type);
            if (p > 0)
                quickSortNotes(A, lo, p-1, type);

            quickSortNotes(A, p+1, hi, type);
        }
    }

    /**
     * Partitions notes according to the current sorting type
     * @param A Array to sort
     * @param lo low index
     * @param hi high index
     * @param type sorting type
     * @return new pivot
     */
    private static int partition(Note[] A, int lo, int hi, SortingType type) {

        int i = 0;
        
        switch (type) {
            case ASCENDINGTAG:
                i = sortTag(A, lo, hi, type);
                break;
            case DESCENDINGTAG:
                i = sortTag(A, lo, hi, type);
                break;
            case ASCENDINGDATE:
                i = sortDate(A, lo, hi, type);
            case DESCENDINGDATE:
                i = sortDate(A, lo, hi, type);
            default:
                break;
        }
        
        Note temp = A[i];
        A[i] = A[hi];
        A[hi] = temp;

        return i;
    }

    /**
     * Switches notes to their own sides according to if their tag is 
     * bigger or smaller than a chosen pivot
     * @param A Array to sort
     * @param lo low index
     * @param hi high index
     * @param type sorting type
     * @return new pivot
     */
    private static int sortTag(Note[] A, int lo, int hi, SortingType type) {
        int i = lo;
        Tag pivot = A[hi].getTag();
        for (int j = lo; j < hi; j++) {
            switch(type) {
                case ASCENDINGTAG:
                    if (A[j].getTag().ordinal() > pivot.ordinal()) {
                        Note temp = A[i];
                        A[i] = A[j];
                        A[j] = temp;
                        i++;
                    }
                    break;
                case DESCENDINGTAG:
                    if (A[j].getTag().ordinal() < pivot.ordinal()) {
                        Note temp = A[i];
                        A[i] = A[j];
                        A[j] = temp;
                        i++;
                    }
                    break;
                default:
                    break;
            }
        }

        return i;
    }

    /**
     * Switches notes to their own sides according to if their date is 
     * bigger or smaller than a chosen pivot
     * @param A Array to sort
     * @param lo low index
     * @param hi high index
     * @param type sorting type
     * @return new pivot
     */
    private static int sortDate(Note[] A, int lo, int hi, SortingType type) {
        int i = lo;
        Date pivot = A[hi].getDate();
        for (int j = lo; j < hi; j++) {
            switch(type) {
                case ASCENDINGDATE:
                    if (A[j].getDate().before(pivot)) {
                        Note temp = A[i];
                        A[i] = A[j];
                        A[j] = temp;
                        i++;
                    }
                    break;
                case DESCENDINGDATE:
                    if (A[j].getDate().after(pivot)) {
                        Note temp = A[i];
                        A[i] = A[j];
                        A[j] = temp;
                        i++;
                    }
                    break;
                default:
                    break;
            }
        }

        return i;
    }

}
