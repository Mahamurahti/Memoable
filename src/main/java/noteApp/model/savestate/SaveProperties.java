package noteApp.model.savestate;

import java.io.*;
import java.util.Properties;

/**
 * Used to access properties file.
 * @author Matias Vainio
 */
public class SaveProperties {
    private static final String PROP_PATH = "src/main/resources/props/save.properties";

    /**
     * Creates a entry in properties file which specifies where notes are saved on user's computer.
     * @param path {String} path to be set.
     */
    public static void create(String path) {
        File configFile = new File(PROP_PATH);
        Properties props = new Properties();
        props.setProperty("default-path", path);
        try {
            FileWriter fw = new FileWriter(configFile);
            props.store(fw, "save-settings");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns set path from properties file.
     * @return path on user's computer in String format.
     */
    public static String getPath() {
        File configFile = new File(PROP_PATH);
        Properties props = new Properties();
        try {
            FileReader fr = new FileReader(configFile);
            props.load(fr);
            return props.getProperty("default-path");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
