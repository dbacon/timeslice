package com.enokinomi.timeslice.lib.ordering.api;

import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;

public interface IOrderingWorks
{

    IConnectionWork<List<String>> workRequestOrdering(String setName, List<String> unorderedSetValues);
    IConnectionWork<Void> workSetOrdering(String setName, List<String> orderedSetMembers);
    IConnectionWork<Void> workAddPartialOrdering(String setName, String smaller, List<String> larger);

}
