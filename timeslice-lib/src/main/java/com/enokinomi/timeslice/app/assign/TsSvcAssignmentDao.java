package com.enokinomi.timeslice.app.assign;

import com.enokinomi.timeslice.timeslice.TimesliceApp;

public class TsSvcAssignmentDao implements IAssignmentDao
{
    private final TimesliceApp tsApp;
    private final INowProvider nowProvider;

    public TsSvcAssignmentDao(TimesliceApp tsApp, INowProvider nowProvider)
    {
        this.tsApp = tsApp;
        this.nowProvider = nowProvider;
    }

    @Override
    public void assign(String description, String billTo)
    {
        tsApp.getFrontStore().assignBillee(description, billTo, nowProvider.getNow());
    }

    @Override
    public String getBillee(String description, String valueIfNotAssigned)
    {
        return tsApp.getFrontStore().lookupBillee(description, nowProvider.getNow());
    }

}
