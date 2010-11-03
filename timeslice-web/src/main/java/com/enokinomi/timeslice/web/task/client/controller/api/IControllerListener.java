package com.enokinomi.timeslice.web.task.client.controller.api;

import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.util.AsyncResult;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;


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
