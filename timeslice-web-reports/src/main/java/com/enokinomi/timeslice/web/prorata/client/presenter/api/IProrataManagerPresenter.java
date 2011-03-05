package com.enokinomi.timeslice.web.prorata.client.presenter.api;

import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.prorata.client.core.Group;
import com.enokinomi.timeslice.web.prorata.client.ui.impl.ProjectProrataTreePanel.Row;

public interface IProrataManagerPresenter
{
    public static interface Listener
    {
        void allGroupInfoChanged(List<Group> result);

        void removeComplete(String group, String name);

        void addComplete(String group, String name);
        void addFailed(String msg);

        void tasksUpdated();
    }


    // TODO: this is going away - for now allow another service to tell us some data.
    void setStuff(List<AssignedTaskTotal> report);

    Registration addListener(Listener listener);

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
