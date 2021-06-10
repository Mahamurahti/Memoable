package noteApp.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Class for handling currently set locales
 * @author Nico Järvinen
 */
public class Context {

    private final List<Locale> availableLocales;

    private Locale currentLocale;

    private static Context instance;

    /**
     * Returns the instance of the class
     * @return instance
     */
    public static Context getInstance() {
        if (instance == null) {
            instance = new Context();
        }
        return instance;
    }

    /**
     * Creates the available locales
     */
    private Context() {
        availableLocales = new LinkedList<>();
        availableLocales.add(new Locale("fi"));
        availableLocales.add(new Locale("en"));
        availableLocales.add(new Locale("ru"));
        availableLocales.add(new Locale("es"));
        currentLocale = new Locale("en");  // default locale
    }

    /**
     * This method is used to return available locales
     *
     * @return available locales
     */
    public List<Locale> getAvailableLocales() {
        return availableLocales;
    }

    /**
     * This method is used to return current locale setting
     *
     * @return current locale
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * This method is used to set current locale setting
     *
     * @param index language index
     */
    public void setCurrentLocale(int index) {
        currentLocale = availableLocales.get(index);
    }
}