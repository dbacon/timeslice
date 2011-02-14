package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.google.gwt.user.client.ui.IsWidget;

public interface IReportPanel extends IsWidget
{
    IParamPanel getParamsPanel();
    void addReportPanelListener(IReportPanelListener iReportPanelListener);
    void setResultsAssigned(List<AssignedTaskTotal> report);
    void setResults(List<TaskTotal> results);
    public abstract void setBillees(List<String> billees);
    void clear();
    void bindProrataBits(IProrataManagerPresenter prorataPresenter, ISettingsPresenter presenter);
    void bind(ISettingsPresenter settingsPresenter);
    void update();
}
