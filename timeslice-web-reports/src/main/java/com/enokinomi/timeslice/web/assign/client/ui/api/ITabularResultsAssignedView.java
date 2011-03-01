package com.enokinomi.timeslice.web.assign.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.google.gwt.user.client.ui.IsWidget;

public interface ITabularResultsAssignedView extends IsWidget
{
    public interface ITabularResultsAssignedViewListener
    {
        void billeeUpdate(String description, String newBillee);
    }

    Registration addListener(ITabularResultsAssignedViewListener listener);

    void setResults(List<AssignedTaskTotal> report);

    void setBillees(List<String> billees);

    void clear();

}
