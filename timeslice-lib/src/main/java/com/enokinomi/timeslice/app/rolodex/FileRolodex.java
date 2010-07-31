package com.enokinomi.timeslice.app.rolodex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.enokinomi.timeslice.lib.util.Narrow;


public class FileRolodex implements IRolodex
{
    private final File file;

    public File getFile()
    {
        return file;
    }

    public FileRolodex(File file)
    {
        this.file = file;
    }

    @Override
    public List<ClientInfo> getClientInfos()
    {
        try
        {
            List<ClientInfo> result = new ArrayList<ClientInfo>();
            for (String name: Narrow.<String>fromList(FileUtils.readLines(getFile(), "UTF-8")))
            {
                result.add(new ClientInfo(name));
            }
            return result;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not read rolodex entries from file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isWritable()
    {
        return true;
    }

    private static final String LineSeparator = System.getProperty("line.separator");

    @Override
    public void addClientInfo(ClientInfo clientInfo)
    {
        if (clientInfo.getName().contains(LineSeparator))
        {
            throw new RuntimeException("Name contains a line-separator");
        }

        BufferedWriter bw = null;
        try
        {
            bw = new BufferedWriter(new FileWriter(getFile(), true));
            bw.write(clientInfo.getName());
            bw.write(LineSeparator);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not add name to file rolodex: " + e.getMessage(), e);
        }
        finally
        {
            if (null != bw)
            {
                try
                {
                    bw.close();
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Could not clean-up rolodex file: " + e.getMessage(), e);
                }
            }
        }
    }
}
