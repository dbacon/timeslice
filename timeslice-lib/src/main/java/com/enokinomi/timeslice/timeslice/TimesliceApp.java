package bacond.timeslicer.timeslice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.Instant;

import bacond.timeslicer.app.core.ITimesliceStore;
import bacond.timeslicer.app.core.Split;
import bacond.timeslicer.app.core.StartTag;
import bacond.timeslicer.app.core.StartTagIo;

public class TimesliceApp
{
    public static final String Key_Upgrade = "upgrade";

    private final List<ITimesliceStore> stores = new LinkedList<ITimesliceStore>();

    public void pushFront(ITimesliceStore store)
    {
        stores.add(0, store);
    }

    public void pushBack(ITimesliceStore store)
    {
        stores.add(store);
    }

    public static interface Visitor<T>
    {
        void visit(T t);
    }

    public void forEachStore(Visitor<ITimesliceStore> visitor)
    {
        for(ITimesliceStore store: stores)
        {
            visitor.visit(store);
        }
    }

    public void disableAllStores()
    {
        forEachStore(new Visitor<ITimesliceStore>()
                {
                    @Override
                    public void visit(ITimesliceStore t)
                    {
                        try
                        {
                            if (t.isEnabled())
                            {
                                t.disable();
                            }
                        }
                        catch (RuntimeException e)
                        {
                            System.out.println("WARNING: Disabling failed: " + e.getMessage());
                        }
                    }
                });
    }

    public ITimesliceStore getFrontStore()
    {
        if (stores.size() <= 0)
        {
            throw new RuntimeException("No store.");
        }

        return stores.get(0);
    }

    public int storeCount()
    {
        return stores.size();
    }

    private String aclFileName;
    private String safeDir;
    private String updateUrl;
    private int tzOffset;
    private final StartTagIo startTagIo;
    private final Split splitter;
    private String reportPrefix = "";


    public TimesliceApp(String aclFilename, String safeDir, String updateUrl, StartTagIo startTagIo, Split splitter)
    {
        this(aclFilename, safeDir, updateUrl, 0, startTagIo, splitter);
    }

    public TimesliceApp(String aclFilename, String safeDir, String updateUrl, int tzOffset, StartTagIo startTagIo, Split splitter)
    {
        this.aclFileName = aclFilename;
        this.safeDir = safeDir;
        this.updateUrl = updateUrl;
        this.tzOffset = tzOffset;
        this.startTagIo = startTagIo;
        this.splitter = splitter;
    }

    public String getReportPrefix()
    {
        return reportPrefix;
    }

    public void setReportPrefix(String reportPrefix)
    {
        this.reportPrefix = reportPrefix;
    }

    public int getTzOffset()
    {
        return tzOffset;
    }

    public void setTzOffset(int tzOffset)
    {
        this.tzOffset = tzOffset;
    }

    public String getAclFileName()
    {
        return aclFileName;
    }

    public void setAclFileName(String aclFileName)
    {
        this.aclFileName = aclFileName;
    }

    public String getSafeDir()
    {
        return safeDir;
    }

    public void setSafeDir(String safeDir)
    {
        this.safeDir = safeDir;
    }

    public String getUpdateUrl()
    {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl)
    {
        this.updateUrl = updateUrl;
    }

    public boolean canSaveLoad()
    {
        return null != getSafeDir();
    }

    private File findBackupFile(String key)
    {
        return new File(FilenameUtils.concat(getSafeDir(), "backup-" + key + ".dat"));
    }

    public TimesliceApp preload(boolean doPreload)
    {
        // todo: switch to preload all selected stores
        if (doPreload)
        {
            if (!canSaveLoad())
            {
                throw new RuntimeException("Pre-load requested, but no safe-dir available to save/load.");
            }

            File backupFile = findBackupFile(Key_Upgrade);
            try
            {
                List<StartTag> preloadItems = startTagIo.readItems(new FileInputStream(backupFile));

                getFrontStore().addAll(preloadItems, true);

                System.out.println("Pre-loaded " + preloadItems.size() + " item(s) from '" + backupFile + "'.");
            }
            catch (IOException e)
            {
                System.err.println("Could not pre-load file '" + backupFile + "': " + e.getMessage());
            }
        }

        return this;
    }

    public List<StartTag> queryForTags(String who, Boolean sortReverse, Instant minDate, Instant maxDate, int pageSize, int pageIndex)
    {
        if (stores.size() > 0)
        {
            // todo: switch to query all stores.
            return splitter.split(
                    getFrontStore().query(
                            who,
                            minDate,
                            maxDate,
                            pageSize,
                            pageIndex),
                            new Instant());
        }
        else
        {
            return Collections.emptyList();
        }
    }

}
