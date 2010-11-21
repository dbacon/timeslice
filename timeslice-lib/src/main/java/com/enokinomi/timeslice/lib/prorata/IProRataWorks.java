package com.enokinomi.timeslice.lib.prorata;

import java.math.BigDecimal;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;
import com.enokinomi.timeslice.lib.util.Pair;

public interface IProRataWorks
{

    ConnectionWork<Void> workAddComponent(String groupName, String componentName, BigDecimal weight);
    ConnectionWork<Void> workRemoveComponent(String groupName, String componentName);
    ConnectionWork<List<String>> workListGroupNames();
    ConnectionWork<List<GroupComponent>> workDereferenceGroup(String groupName);
    ConnectionWork<List<Pair<String, List<GroupComponent>>>> workListAllGroupsInfo();

}
