package com.enokinomi.timeslice.web.assign.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.google.gwt.user.client.ui.IsWidget;

public interface ITabularResultsAssignedView extends IsWidget
{

    void addListener(ITabularResultsAssignedViewListener listener);

    void removeListener(ITabularResultsAssignedViewListener listener);

    void setResults(List<AssignedTaskTotal> report);

    void setBillees(List<String> billees);

    void clear();

}
