package com.enokinomi.timeslice.app.assign;

import com.enokinomi.timeslice.app.core.ITagStore;
import com.google.inject.Inject;

public class TsSvcAssignmentDao implements IAssignmentDao
{
    private final INowProvider nowProvider;
    private final ITagStore tagStore;

    @Inject
    public TsSvcAssignmentDao(ITagStore tagStore, INowProvider nowProvider)
    {
        this.tagStore = tagStore;
        this.nowProvider = nowProvider;
    }

    @Override
    public void assign(String description, String billTo)
    {
        tagStore.assignBillee(description, billTo, nowProvider.getNow());
    }

    @Override
    public String getBillee(String description, String valueIfNotAssigned)
    {
        return tagStore.lookupBillee(description, nowProvider.getNow());
    }

}
