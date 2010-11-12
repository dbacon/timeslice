package com.enokinomi.timeslice.web.assign.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.ui.impl.TabularResultsAssignedView.Listener;
import com.enokinomi.timeslice.web.task.client.ui.api.IIsWidget;

public interface ITabularResultsAssignedView extends IIsWidget
{

    void addListener(Listener listener);

    void removeListener(Listener listener);

    void setResults(List<AssignedTaskTotal> report);

    void setBillees(List<String> billees);

}
