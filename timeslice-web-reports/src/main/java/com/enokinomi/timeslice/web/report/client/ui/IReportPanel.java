package com.enokinomi.timeslice.web.report.client.ui;

import java.util.Date;
import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.core.TaskTotal;
import com.enokinomi.timeslice.web.core.client.ui.IClearable;
import com.enokinomi.timeslice.web.core.client.ui.Initializable;
import com.enokinomi.timeslice.web.core.client.ui.NavPanel;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.google.gwt.user.client.ui.IsWidget;

public interface IReportPanel extends IsWidget, IClearable, Initializable
{
    public interface IReportPanelListener
    {
        void refreshRequested(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords);
        void billeeUpdateRequested(String description, String newBillee);
        void itemHistoryRequested(Date when);
    }

    Registration addReportPanelListener(IReportPanelListener iReportPanelListener);
    List<Registration> bindProrataBits(IProrataManagerPresenter prorataPresenter, ISettingsPresenter settingsPresenter);
    List<Registration> bindSubListeners();

    IParamPanel getParamsPanel();
    NavPanel getNavPanel();
    TreeTableResultsView getTreeTable();

    void setResultsAssigned(List<AssignedTaskTotal> report);
    void setResults(List<TaskTotal> results);
    void setBillees(List<String> billees);
    void clear();
    void update();
    void setFullDaySelected(Date when, boolean fireEvents);
    void selectTab(String name, boolean fireEvents);
}
