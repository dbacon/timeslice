package com.enokinomi.timeslice.lib.appjob.api;

import java.util.List;


public interface IAppJobProcessor
{

    List<String> getAvailableJobIds();

    AppJobCompletion performJob(String jobId);

}
