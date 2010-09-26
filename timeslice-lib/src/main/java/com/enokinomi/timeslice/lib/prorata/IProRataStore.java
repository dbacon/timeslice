package com.enokinomi.timeslice.lib.prorata;

import java.math.BigDecimal;
import java.util.List;

public interface IProRataStore
{

    List<GroupComponent> dereferenceGroup(String groupName);

    List<String> listGroupNames();

    List<List<GroupComponent>> listAllGroupsInfo();

    void removeComponent(String groupName, String componentName);

    void addComponent(String groupName, String componentName, BigDecimal weight);

}
