package com.enokinomi.timeslice.lib.ordering;

import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;

public interface IOrderingWorks
{

    ConnectionWork<List<String>> workRequestOrdering(String setName, List<String> unorderedSetValues);
    ConnectionWork<Void> workSetOrdering(String setName, List<String> orderedSetMembers);
    ConnectionWork<Void> workAddPartialOrdering(String setName, String smaller, List<String> larger);

}
