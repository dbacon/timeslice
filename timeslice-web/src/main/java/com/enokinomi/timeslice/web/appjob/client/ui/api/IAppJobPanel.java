package com.enokinomi.timeslice.web.appjob.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.ui.IIsWidget;

public interface IAppJobPanel extends IIsWidget
{
    void addResult(String jobId, String status, String result);
    void addListener(IAppJobPanelListener listener);
    void redisplayJobIds(List<String> jobIds);
}
