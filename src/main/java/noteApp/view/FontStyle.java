package noteApp.view;

import java.util.Arrays;

/**
 * Class used to store font styling information. Style is added to element via toCss() method which
 * returns a valid CSS string according to values set from the ui.
 *
 * @author Matias Vainio
 */
public class FontStyle {
    public static final int FONT_SMALL = 16;
    public static final int FONT_MED = 22;
    public static final int FONT_LARGE = 28;

    private String font;
    private int fontSize;
    private boolean bold, cursive, underline;

    /**
     * Constructor to set default font styling.
     */
    public FontStyle() {
        this.fontSize = FONT_SMALL;
        this.font = "none";
        this.bold = false;
        this.cursive = false;
        this.underline = false;
    }

    /**
     * Returns current font.
     * @return {String} font name to be returned.
     */
    public String getFont() {
        return font;
    }

    /**
     * Sets current font.
     * @param font {String} font name to be set
     */
    public void setFont(String font) {
        this.font = font;
    }

    /**
     * Returns current font size.
     * @return font size to be returned.
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Sets current font size.
     * @param size {String} size to be set.
     */
    public void setFontSize(int size) {
        this.fontSize = size;
    }

    /**
     * Returns whether current style is bolded.
     * @return true if style is bold.
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * Sets bold value.
     * @param bold {boolean} value to be set.
     */
    public void setBold(boolean bold) {
        this.bold = bold;
    }

    /**
     * Returns whether current style is cursive.
     * @return ture if style is cursive.
     */
    public boolean isCursive() {
        return cursive;
    }

    /**
     * Sets cursive value.
     * @param cursive {boolean} value to be set.
     */
    public void setCursive(boolean cursive) {
        this.cursive = cursive;
    }

    /**
     * Returns whether current style is underlined.
     * @return true if style is underlined.
     */
    public boolean isUnderline() {
        return underline;
    }

    /**
     * Set underline value.
     * @param underline {boolean} value to be set.
     */
    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    /**
     * Parse styling values from provided string. Used to get old styling from view which is then modified and
     * used to style the text again.
     * @param styleString {String} String to be parsed.
     */
    public void setFromStyleString(String styleString) {
        String[] pieces = styleString.split(" ");
        this.fontSize = Integer.parseInt(pieces[1]);
        setBold(!pieces[4].equals("none;"));
        setCursive(!pieces[6].equals("none;"));
        setFont(pieces[8].split("'")[1]);
        setUnderline(!pieces[11].equals("false;"));
    }

    /**
     * Resets styling to default.
     */
    public void resetStyling() {
        this.fontSize = FONT_SMALL;
        this.font = "none";
        this.bold = false;
        this.cursive = false;
        this.underline = false;
    }

    /**
     * Main method of the class. Creates a CSS String which is used to style text objects in the view.
     * Conditionally appends styling information to string and returns it.
     * @return CSS String to be returned.
     */
    public String toCss() {
        StringBuilder sb = new StringBuilder();

        sb.append("-fx-font-size: ").append(fontSize).append(" px; ");
        if (isBold()) {
            sb.append("-fx-font-weight: bold; ");
        } else {
            sb.append("-fx-font-weight: none; ");
        }

        if (isCursive()) {
            sb.append("-fx-font-style: italic; ");
        } else {
            sb.append("-fx-font-style: none; ");
        }

        sb.append("-fx-font-family: '").append(font).append("' ; ");

        if (isUnderline()) {
            sb.append("-fx-underline: true;");
        } else {
            sb.append("-fx-underline: false;");
        }

        return sb.toString();
    }
}
