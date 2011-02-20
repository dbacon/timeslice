package com.enokinomi.timeslice.web.task.client.ui.api;

import java.util.Date;
import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.ui.FooterPanel;
import com.enokinomi.timeslice.web.core.client.ui.IClearable;
import com.enokinomi.timeslice.web.core.client.ui.Initializable;
import com.enokinomi.timeslice.web.core.client.ui.Registration;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.google.gwt.user.client.ui.IsWidget;

public interface IReportPanel extends IsWidget, IClearable, Initializable
{
    IParamPanel getParamsPanel();
    FooterPanel getFooterPanel();
    Registration addReportPanelListener(IReportPanelListener iReportPanelListener);
    void setResultsAssigned(List<AssignedTaskTotal> report);
    void setResults(List<TaskTotal> results);
    void setBillees(List<String> billees);
    void clear();
    void update();
    void setFullDaySelected(Date when, boolean fireEvents);
    void selectTab(String name, boolean fireEvents);
    void bindProrataBits(IProrataManagerPresenter prorataPresenter, ISettingsPresenter settingsPresenter);

//    void bind(ISettingsPresenter settingsPresenter);
}
