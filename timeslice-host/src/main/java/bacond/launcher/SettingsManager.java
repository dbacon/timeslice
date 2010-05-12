package bacond.launcher;

import java.io.File;
import java.util.Properties;

/**
 * Generic utility class to load settings from properties files and query them.
 *
 * @author dbacon
 *
 * @param <D> Derived type which {@code this} should be down-cast before returning when used for supporting
 * factory methods, such as {@code pushSettings}.
 *
 */
public class SettingsManager<D extends SettingsManager<?>>
{
    private Properties settings;

    @SuppressWarnings("unchecked")
    private static <D extends SettingsManager<?>> D narrow(SettingsManager<D> g)
    {
        return (D) g;
    }

    protected Properties getSettings()
    {
        return settings;
    }

    protected void setSettings(Properties settings)
    {
        this.settings = settings;
    }

    public SettingsManager(Properties defaults)
    {
        this.settings = defaults;
    }

    public D pushSettings(File file, boolean require)
    {
        setSettings(Util.loadSettings(file, getSettings(), require));
        return narrow(this);
    }

    public D pushSettings(String fileName, boolean require)
    {
        return pushSettings(new File(fileName), require);
    }

    public D pushSettings(String[] fileNames, boolean require)
    {
        for (String fileName: fileNames)
        {
            pushSettings(fileName, require);
        }

        return narrow(this);
    }

    public void getOrDie(String name)
    {
        if (!getSettings().containsKey(name))
        {
            throw new RuntimeException("Required setting '" + name + "' was not found.");
        }
    }

    public String getOrDefault(String name, String defaultValue)
    {
        return getSettings().getProperty(name, defaultValue);
    }

    public Integer getOrDefaultInt(String name, Integer defaultValue)
    {
        if (null == defaultValue)
        {
            String result = getOrDefault(name, null);
            if (null == result) return null;
            return Integer.valueOf(result);
        }
        else
        {
            return Integer.valueOf(getOrDefault(name, defaultValue.toString()));
        }
    }

}
