package com.enokinomi.timeslice.web.appjob.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.ui.IClearable;
import com.enokinomi.timeslice.web.core.client.ui.Initializable;
import com.enokinomi.timeslice.web.core.client.ui.NavPanel;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.google.gwt.user.client.ui.IsWidget;

public interface IAppJobPanel extends IsWidget, IClearable, Initializable
{
    public interface IAppJobPanelListener
    {
        void appJobRequested(String jobId);
        void appJobListRefreshRequested();
    }

    Registration addListener(IAppJobPanelListener listener);

    NavPanel getNavPanel();
    void addResult(String jobId, String status, String result);
    void redisplayJobIds(List<String> jobIds);
}
