package bacond.timeslicer.timeslice.entry;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

/**
 * Implements reading settings from a {@link File}.
 *
 * @author dbacon
 *
 */
public class GenericSettingsProvider implements ISettingsProvider
{
    private final String filename;

    public GenericSettingsProvider(String filename)
    {
        this.filename = filename;
    }

    /**
     *
     * <p>
     * If {@code null} is passed, it will be replaced
     * with the default rc filename calculated for the platform
     * (e.g. {@code ~/.timeslicerc}).
     * </p>
     *
     * @param filename
     * @return
     */
    @Override
    public Properties readSettings(Properties settings)
    {
        File rcfile = new File(filename);

        System.out.println("Reading settings file: " + rcfile);

        try
        {
            settings.load(new FileInputStream(rcfile));
        }
        catch (Exception e)
        {
            System.err.println("Exception reading settings: " + e.getMessage());
        }

        return settings;
    }

    public static String calculateDefaultRcFilename()
    {
        String homeDir = System.getProperty("user.home", ".");

        String rcFilename = FilenameUtils.concat(homeDir, ".timeslicerc");
        return rcFilename;
    }

    @Override
    public void writeSettings(Properties settings)
    {
        throw new RuntimeException("Not implemented.");
    }
}
