package com.enokinomi.timeslice.web.appjob.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.ui.FooterPanel;
import com.enokinomi.timeslice.web.core.client.ui.IClearable;
import com.enokinomi.timeslice.web.core.client.ui.Initializable;
import com.enokinomi.timeslice.web.core.client.ui.Registration;
import com.google.gwt.user.client.ui.IsWidget;

public interface IAppJobPanel extends IsWidget, IClearable, Initializable
{
    FooterPanel getFooterPanel();
    void addResult(String jobId, String status, String result);
    Registration addListener(IAppJobPanelListener listener);
    void redisplayJobIds(List<String> jobIds);
}
