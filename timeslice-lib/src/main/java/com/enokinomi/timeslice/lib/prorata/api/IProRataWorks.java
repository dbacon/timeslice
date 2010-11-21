package com.enokinomi.timeslice.lib.prorata.api;

import java.math.BigDecimal;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.util.Pair;

public interface IProRataWorks
{

    IConnectionWork<Void> workAddComponent(String groupName, String componentName, BigDecimal weight);
    IConnectionWork<Void> workRemoveComponent(String groupName, String componentName);
    IConnectionWork<List<String>> workListGroupNames();
    IConnectionWork<List<GroupComponent>> workDereferenceGroup(String groupName);
    IConnectionWork<List<Pair<String, List<GroupComponent>>>> workListAllGroupsInfo();

}
