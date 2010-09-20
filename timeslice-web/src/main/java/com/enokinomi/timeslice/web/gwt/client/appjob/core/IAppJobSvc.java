package com.enokinomi.timeslice.web.gwt.client.appjob.core;

import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.core.NotAuthenticException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface IAppJobSvc extends RemoteService
{
    List<String> getAvailableJobIds(String authToken) throws NotAuthenticException;
    AppJobCompletion performJob(String jobId) throws NotAuthenticException;
}
