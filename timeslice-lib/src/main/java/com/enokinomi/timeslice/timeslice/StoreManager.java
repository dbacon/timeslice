package com.enokinomi.timeslice.timeslice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.enokinomi.timeslice.app.core.ByWhen;
import com.enokinomi.timeslice.app.core.ITimesliceStore;
import com.enokinomi.timeslice.app.core.MemoryTimesliceStore;


public class StoreManager
{
    private final File storeDir;
    private final List<IParser> plugins;

    public StoreManager(File storeDir, List<IParser> plugins)
    {
        this.storeDir = storeDir;
        this.plugins = plugins;
    }

    public File getStoreDir()
    {
        return storeDir;
    }

    public static interface IParser
    {
        ITimesliceStore parse(StoreDescriptor stuff);
    }

    /**
     * TODO: this is all wiring, move it out.
     *
     * @param tsApp
     * @return
     */
    public ArrayList<ITimesliceStore> configure()
    {
        if (!storeDir.isDirectory() || !storeDir.canRead())
        {
            throw new RuntimeException("Specified store directory is not a directory or not readable: " + storeDir.toString());
        }

        ArrayList<ITimesliceStore> stores = new ArrayList<ITimesliceStore>();

        Collection<?> files = FileUtils.listFiles(storeDir, new String[] { "sd.properties", }, false);
        if (files.size() <= 0)
        {
            System.err.println("WARNING: No files found in store directory '" + storeDir.toString() + "'.");
            return stores;
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

                StoreDescriptor desc = new StoreDescriptor(storeDir, p);

                ITimesliceStore store = null;
                for (IParser parser: plugins)
                {
                    store = parser.parse(desc);
                    if (null != store) break;
                }

                if (null == store)
                {
                    throw new RuntimeException("Unrecognized store definition '" + f.toString() + "', skipping.");
                }

                if (desc.getAutoEnable()) store.enable(false);

                stores.add(store);
            }
            catch (RuntimeException e)
            {
                System.out.printf("WARNING: Skipping '%s': %s\n", f.toString(), e.getMessage());
                continue;
            }
        }

        return stores;
    }

    public static class StoreDescriptor
    {
        private final Properties p;
        private final File storeDir;

        public StoreDescriptor(File storeDir, Properties p)
        {
            this.storeDir = storeDir;
            this.p = p;
        }

        public File getStoreDir()
        {
            return storeDir;
        }

        public String getValue(String key, String defaultValue)
        {
            return p.getProperty(key, defaultValue);
        }

        public String getType()
        {
            return getType("memory");
        }

        public String getType(String defaultValue)
        {
            return p.getProperty("type", defaultValue);
        }

        public String getFirstTagText()
        {
            return p.getProperty("firsttagtext");
        }

        public DateTime getStartingTime()
        {
            return ISODateTimeFormat.dateTime().parseDateTime(p.getProperty("starting"));
        }

        public DateTime getEndingTime()
        {
            return ISODateTimeFormat.dateTime().parseDateTime(p.getProperty("ending"));
        }

        public boolean getAutoEnable()
        {
            return Boolean.valueOf(p.getProperty("autoenable", "false"));
        }
    }

    public static class MemoryPlugin implements IParser
    {
        @Override
        public ITimesliceStore parse(StoreDescriptor desc)
        {
            if (!"memory".equals(desc.getType())) return null;

            return new MemoryTimesliceStore(
                    desc.getStartingTime().toInstant(),
                    desc.getEndingTime().toInstant(),
                    desc.getFirstTagText(),
                    new ByWhen());
        }
    }

    public static class HsqlPlugin implements IParser
    {
        @Override
        public ITimesliceStore parse(StoreDescriptor desc)
        {
            if (!"hsqldb".equals(desc.getType())) return null;

            String name = desc.getValue("hsqldb.name", "timeslicedb/db");
            Integer ddlVer = Integer.valueOf(desc.getValue("hsqldb.ddlversion", "0"));

            String ddlResourceName = String.format("timeslice-%d.ddl", ddlVer);

            return new HsqldbTimesliceStore(
                    new SchemaDuty(ddlVer, ddlResourceName),
                    desc.getStoreDir(),
                    name,
                    desc.getFirstTagText(),
                    desc.getStartingTime().toInstant(),
                    desc.getEndingTime().toInstant());
        }
    }
}
