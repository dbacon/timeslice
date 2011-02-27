package com.enokinomi.timeslice.web.prorata.client.presenter.api;

import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.prorata.client.core.Group;
import com.enokinomi.timeslice.web.prorata.client.presenter.impl.ProrataManagerPresenter.Listener;
import com.enokinomi.timeslice.web.prorata.client.ui.impl.ProjectProrataTreePanel.Row;

public interface IProrataManagerPresenter
{

    // TODO: this is going away - for now allow another service to tell us some data.
    void setStuff(List<AssignedTaskTotal> report);

    void addListener(Listener listener);

    double getGrandTotal();

    Map<String, Double> getLeafTotals();

    void sendPartialOrderingAssignment(Map<String, Double> projectMap, int i, int j);

    List<Group> getGroupInfo();

    void addGroupComponent(String groupName, String target, Double weight);

    void loadAllRules(String text);

    void removeParsedRules(String text);

    void removeGroupComponent(String group, String name);

    List<Row> getRows();

}
