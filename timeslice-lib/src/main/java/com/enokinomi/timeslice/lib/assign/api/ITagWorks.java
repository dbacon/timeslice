package com.enokinomi.timeslice.lib.assign.api;

import java.util.List;

import org.joda.time.DateTime;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;

public interface ITagWorks
{

    IConnectionWork<String> workLookupBillee(final String description, final DateTime asOf, final String valueOnMiss);
    IConnectionWork<Void> workAssignBillee(final String description, final String billee, final DateTime date);
    IConnectionWork<Void> workEndDateAnyBillee(final String description, final DateTime untilDate);
    IConnectionWork<Void> workInsertBillee(final String description, final String billee, final DateTime asOf);
    IConnectionWork<List<String>> workGetAllBillees();

}
