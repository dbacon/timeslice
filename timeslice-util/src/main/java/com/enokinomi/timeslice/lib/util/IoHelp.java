package com.enokinomi.timeslice.lib.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class IoHelp
{
    public String readIt(InputStream is)
    {
        try
        {
            return IOUtils.toString(is);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not read stream to string: " + e.getMessage(), e);
        }
    }

    public String readSystemResource(String resourceName)
    {
        return readIt(ClassLoader.getSystemResourceAsStream(resourceName));
    }
}
