package bacond.timeslicer.timeslice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import bacond.timeslicer.app.core.ITimesliceStore;
import bacond.timeslicer.app.core.MemoryTimesliceStore;

public class StoreManager
{
    private final File storeDir;

    public StoreManager(File storeDir)
    {
        this.storeDir = storeDir;
    }

    public File getStoreDir()
    {
        return storeDir;
    }

    public void configure(TimesliceApp tsApp)
    {
        if (!storeDir.isDirectory() || !storeDir.canRead())
        {
            throw new RuntimeException("Specified store directory is not a directory or not readable: " + storeDir.toString());
        }

        if (null == tsApp)
        {
            System.err.println("WARNING: No TimesliceApp available to configure");
            return;
        }

        Collection<?> files = FileUtils.listFiles(storeDir, new String[] { "sd.properties", }, false);
        if (files.size() <= 0)
        {
            System.err.println("WARNING: No files found in store directory '" + storeDir.toString() + "'.");
            return;
        }

        for (Object o: files)
        {
            File f = (File) o;

            try
            {
                if (!f.isFile() || !f.canRead())
                {
                    System.out.printf("WARNING: Skipping '%s': not readable or not a file.\n", f.toString());
                    continue;
                }

                Properties p = new Properties();
                try
                {
                    p.load(new BufferedInputStream(new FileInputStream(f)));
                }
                catch (IOException e)
                {
                    System.out.printf("WARNING: Skipping '%s': %s\n", f.toString(), e.getMessage());
                    continue;
                }

                String startingStr = p.getProperty("starting");
                String endingStr = p.getProperty("ending");
                String firstTagText = p.getProperty("firsttagtext");
                String autoEnableStr = p.getProperty("autoenable", "false");
                String type = p.getProperty("type", "memory");

                if (null == startingStr || null == endingStr || null == firstTagText)
                {
                    System.out.printf("WARNING: Skipping '%s': mal-formed, starting, ending, or firsttagtext missing.\n", f.toString());
                }

                DateTime startingTime = ISODateTimeFormat.dateTime().parseDateTime(startingStr);
                DateTime endingTime = ISODateTimeFormat.dateTime().parseDateTime(endingStr);
                boolean autoEnable = Boolean.valueOf(autoEnableStr);

                ITimesliceStore store = null;
                if ("memory".equals(type))
                {
                    MemoryTimesliceStore memStore = new MemoryTimesliceStore(startingTime.toInstant(), endingTime.toInstant(), firstTagText);
                    store = memStore;
                }
                else if ("hsqldb".equals(type))
                {
                    String name = p.getProperty("hsqldb.name", "timeslicedb/db");
                    HsqldbTimesliceStore hStore = new HsqldbTimesliceStore(storeDir, name, firstTagText, startingTime.toInstant(), endingTime.toInstant());
                    store = hStore;
                }

                if (null == store)
                {
                    throw new RuntimeException("Unknown store type '" + type + "', skipping.");
                }

                if (autoEnable) store.enable();

                tsApp.pushFront(store);
            }
            catch (RuntimeException e)
            {
                System.out.printf("WARNING: Skipping '%s': %s\n", f.toString(), e.getMessage());
                continue;
            }
        }
    }
}
