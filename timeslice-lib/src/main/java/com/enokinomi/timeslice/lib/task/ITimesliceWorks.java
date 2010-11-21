package com.enokinomi.timeslice.lib.task;

import java.util.List;

import org.joda.time.Instant;

import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;

public interface ITimesliceWorks
{

    ConnectionWork<Void> workAdd(StartTag tag);
    ConnectionWork<List<StartTag>> workQuery(String owner, Instant starting, Instant ending, int pageSize, int pageIndex);
    ConnectionWork<Void> workRemove(StartTag tag);
    ConnectionWork<Void> workUpdateText(StartTag tag);

}
