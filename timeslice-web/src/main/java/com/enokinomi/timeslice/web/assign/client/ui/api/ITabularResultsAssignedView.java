package com.enokinomi.timeslice.web.assign.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.ui.IIsWidget;

public interface ITabularResultsAssignedView extends IIsWidget
{

    void addListener(ITabularResultsAssignedViewListener listener);

    void removeListener(ITabularResultsAssignedViewListener listener);

    void setResults(List<AssignedTaskTotal> report);

    void setBillees(List<String> billees);

}
