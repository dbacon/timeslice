package com.enokinomi.timeslice.web.gwt.client.appjob.core;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IAppJobSvcAsync
{

    void getAvailableJobIds(String authToken, AsyncCallback<List<String>> callback);
    void performJob(String authToken, String jobId, AsyncCallback<AppJobCompletion> callback);

}
