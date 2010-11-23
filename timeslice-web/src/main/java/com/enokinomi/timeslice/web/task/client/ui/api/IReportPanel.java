package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.ui.IIsWidget;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;

public interface IReportPanel extends IIsWidget
{
    IParamPanel getParamsPanel();
    void addReportPanelListener(IReportPanelListener iReportPanelListener);
    void setPersisted(String persistedName);
    void setResultsAssigned(List<AssignedTaskTotal> report);
    void setResults(List<TaskTotal> results);
    public abstract void setBillees(List<String> billees);
}
