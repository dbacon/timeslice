package com.enokinomi.timeslice.web.appjob.client.ui.api;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

public interface IAppJobPanel extends IsWidget
{
    void addResult(String jobId, String status, String result);
    void addListener(IAppJobPanelListener listener);
    void redisplayJobIds(List<String> jobIds);
}
