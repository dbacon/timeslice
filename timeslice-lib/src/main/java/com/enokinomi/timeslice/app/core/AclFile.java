package com.enokinomi.timeslice.app.core;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.enokinomi.timeslice.lib.util.Narrow;


public class AclFile
{
    private final String fileName;

    public AclFile(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String lookupPassword(String name)
    {
        Map<String, String> map = readFileIntoMap();

        if (map.containsKey(name))
        {
            return map.get(name);
        }
        else
        {
            return null;
        }
    }

    private Map<String, String> readFileIntoMap()
    {

        try
        {
            Map<String, String> map = new LinkedHashMap<String, String>();

            for (String line: Narrow.<String>fromList(FileUtils.readLines(new File(getFileName()))))
            {
                String[] fields = line.split(":");

                if (2 == fields.length)
                {
                    String user = fields[0];
                    String pass = fields[1];

                    map.put(user, pass);
                }
            }

            return map;

        }
        catch (IOException e)
        {
            throw new RuntimeException("Exception reading password file: " + e.getMessage(), e);
        }
    }
}
