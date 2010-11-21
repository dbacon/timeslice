package com.enokinomi.timeslice.lib.assign;

import java.util.List;

import org.joda.time.DateTime;

import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.google.inject.Inject;

public class HsqldbTagStore implements ITagStore
{
    private final IConnectionContext connContext;
    private final ITagWorks tagWorks;

    @Inject
    public HsqldbTagStore(ITagWorks tagWorks, IConnectionContext connContext)
    {
        this.tagWorks = tagWorks;
        this.connContext = connContext;
    }

    @Override
    public String lookupBillee(final String description, final DateTime asOf, final String valueOnMiss)
    {
        return connContext.doWorkWithinContext(tagWorks.workLookupBillee(description, asOf, valueOnMiss));
    }

    @Override
    public void assignBillee(final String description, final String billee, final DateTime date)
    {
        connContext.doWorkWithinContext(tagWorks.workAssignBillee(description, billee, date));
    }

    @Override
    public List<String> getAllBillees()
    {
        return connContext.doWorkWithinContext(tagWorks.workGetAllBillees());
    }
}
