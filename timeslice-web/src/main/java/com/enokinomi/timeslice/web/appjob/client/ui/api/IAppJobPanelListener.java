package com.enokinomi.timeslice.web.appjob.client.ui.api;

public interface IAppJobPanelListener
{
    void appJobRequested(String jobId);

    void appJobListRefreshRequested();
}
