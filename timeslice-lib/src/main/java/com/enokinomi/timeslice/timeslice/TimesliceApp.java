package com.enokinomi.timeslice.timeslice;

import com.enokinomi.timeslice.app.core.ITagStore;


@Deprecated
public class TimesliceApp
{
    private final ITagStore tagStore;
    private String safeDir;
    private String reportPrefix = "";

    public ITagStore getTagStore()
    {
        return tagStore;
    }

    public TimesliceApp(ITagStore tagStore, String safeDir)
    {
        this.tagStore = tagStore;
        this.safeDir = safeDir;
    }

    public String getReportPrefix()
    {
        return reportPrefix;
    }

    public void setReportPrefix(String reportPrefix)
    {
        this.reportPrefix = reportPrefix;
    }

    public String getSafeDir()
    {
        return safeDir;
    }

    public void setSafeDir(String safeDir)
    {
        this.safeDir = safeDir;
    }

//    public List<StartTag> queryForTags(String who, Boolean sortReverse, Instant minDate, Instant maxDate, int pageSize, int pageIndex)
//    {
//        return splitter.split(
//                store.query(
//                        who,
//                        minDate,
//                        maxDate,
//                        pageSize,
//                        pageIndex),
//                        new Instant()); // TODO: change to now-provider
//    }

}
