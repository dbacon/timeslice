package com.enokinomi.timeslice.lib.prorata.api;

import java.math.BigDecimal;
import java.util.List;

import com.enokinomi.timeslice.lib.util.Pair;

public interface IProrataStore
{

    List<GroupComponent> dereferenceGroup(String groupName);

    List<String> listGroupNames();

    List<Pair<String, List<GroupComponent>>> listAllGroupsInfo();

    void removeComponent(String groupName, String componentName);

    void addComponent(String groupName, String componentName, BigDecimal weight);

}
