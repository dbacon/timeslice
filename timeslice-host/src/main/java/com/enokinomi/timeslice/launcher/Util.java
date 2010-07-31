package com.enokinomi.timeslice.launcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public class Util
{

    /**
     * Copies any name/value pair in {@code props} into {@code dest} if the key has the specified prefix {@code prefix}.
     *
     * @param props
     * @param prefix
     * @param dest
     */
    public static void copyPrefixed(Properties props, String prefix, Map<String, String> dest)
    {
        for (Entry<Object, Object> property: props.entrySet())
        {
            if (property.getKey().toString().startsWith(prefix))
            {
                dest.put(property.getKey().toString(), property.getValue().toString());
            }
        }
    }

    /**
     * If {@code required} is {@code true}, throw a {@code RuntimeException}
     * with the specified message {@code msg}, otherwise log the specified message
     * (currently by printing to {@code System.out}).
     *
     * @param require
     * @param msg
     */
    public static void throwOrLog(boolean require, String msg)
    {
        if (require)
        {
            throw new RuntimeException(msg);
        }
        else
        {
            System.out.println("INFO: " + msg);
        }
    }

    /**
     * Creates and returns a {@link Properties} containing name/value pairs
     * loaded from the specified file {@code rcfile}.
     *
     * <p>
     * If the file cannot
     * be read and {@code require} is {@code true}, a {@code RuntimeException} is thrown.
     * </p>
     *
     * <p>
     * If {@code defaults} is not {@code null}, it is used as the defaults in the returned
     * {@code Properties}.
     * </p>
     *
     * @param rcfile
     * @param defaults
     * @param require
     * @return
     */
    public static Properties loadSettings(File rcfile, Properties defaults, boolean require)
    {
        Properties props = null == defaults ? new Properties() : new Properties(defaults);

        if (rcfile.canRead() && rcfile.isFile())
        {
            try
            {
                props.load(new BufferedInputStream(new FileInputStream(rcfile)));
            }
            catch (IOException e)
            {
                throwOrLog(require, "Settings file '" + rcfile.toString() + "' could not be loaded: " + e.getMessage());
            }
        }
        else
        {
            throwOrLog(require, "Settings file is not readable or is not a file: '" + rcfile.toString() + "'.");
        }

        return props;
    }

}
