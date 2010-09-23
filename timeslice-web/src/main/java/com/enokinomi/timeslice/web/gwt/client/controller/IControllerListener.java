package com.enokinomi.timeslice.web.gwt.client.controller;

import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.appjob.core.AppJobCompletion;
import com.enokinomi.timeslice.web.gwt.client.assigned.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.core.AsyncResult;
import com.enokinomi.timeslice.web.gwt.client.core.BrandInfo;
import com.enokinomi.timeslice.web.gwt.client.task.core.StartTag;
import com.enokinomi.timeslice.web.gwt.client.task.core.TaskTotal;


public interface IControllerListener
{
    void serverInfoRecieved(String info);
    void onBranded(AsyncResult<BrandInfo> result);
    void authenticated();
    void unauthenticated(boolean retry);
    void onRefreshItemsDone(AsyncResult<List<StartTag>> result);
    void onAddItemDone(AsyncResult<Void> result);
    void onRefreshTotalsDone(AsyncResult<List<TaskTotal>> result);
    void onPersistTotalsDone(AsyncResult<String> result);
    void onRefreshTotalsAssignedDone(AsyncResult<List<AssignedTaskTotal>> result);
    void onAssignBilleeDone(AsyncResult<Void> result);
    void onAllBilleesDone(AsyncResult<List<String>> asyncResult);

    void onListAvailableJobsDone(AsyncResult<List<String>> result);
    void onPerformJobDone(AsyncResult<AppJobCompletion> asyncResult);
}
