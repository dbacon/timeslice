package com.enokinomi.timeslice.lib.assign;

import java.util.List;

import org.joda.time.DateTime;

import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;

public interface ITagWorks
{

    ConnectionWork<String> workLookupBillee(final String description, final DateTime asOf, final String valueOnMiss);
    ConnectionWork<Void> workAssignBillee(final String description, final String billee, final DateTime date);
    ConnectionWork<Void> workEndDateAnyBillee(final String description, final DateTime untilDate);
    ConnectionWork<Void> workInsertBillee(final String description, final String billee, final DateTime asOf);
    ConnectionWork<List<String>> workGetAllBillees();

}
