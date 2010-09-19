package com.enokinomi.timeslice.web.gwt.client.server;

import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.beans.AppJobCompletion;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IAppJobSvcAsync
{

    void getAvailableJobIds(String authToken, AsyncCallback<List<String>> callback);
    void performJob(String jobId, AsyncCallback<AppJobCompletion> callback);

}
