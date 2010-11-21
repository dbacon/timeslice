package com.enokinomi.timeslice.lib.task.api;

import java.util.List;

import org.joda.time.Instant;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;

public interface ITimesliceWorks
{

    IConnectionWork<Void> workAdd(StartTag tag);
    IConnectionWork<List<StartTag>> workQuery(String owner, Instant starting, Instant ending, int pageSize, int pageIndex);
    IConnectionWork<Void> workRemove(StartTag tag);
    IConnectionWork<Void> workUpdateText(StartTag tag);

}
