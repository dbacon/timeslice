package com.enokinomi.timeslice.web.task.client.ui_one.impl;

import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.util.AsyncResult;
import com.enokinomi.timeslice.web.task.client.controller.api.IControllerListener;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;

public class ControllerListenerAdapter implements IControllerListener
{

    @Override
    public void serverInfoRecieved(String info)
    {
    }

    @Override
    public void onBranded(AsyncResult<BrandInfo> result)
    {
    }

    @Override
    public void authenticated()
    {
    }

    @Override
    public void unauthenticated(boolean retry)
    {
    }

    @Override
    public void onRefreshItemsDone(AsyncResult<List<StartTag>> result)
    {
    }

    @Override
    public void onAddItemDone(AsyncResult<Void> result)
    {
    }

    @Override
    public void onRefreshTotalsDone(AsyncResult<List<TaskTotal>> result)
    {
    }

    @Override
    public void onPersistTotalsDone(AsyncResult<String> result)
    {
    }

    @Override
    public void onRefreshTotalsAssignedDone(AsyncResult<List<AssignedTaskTotal>> result)
    {
    }

    @Override
    public void onAssignBilleeDone(AsyncResult<Void> result)
    {
    }

    @Override
    public void onAllBilleesDone(AsyncResult<List<String>> asyncResult)
    {
    }

    @Override
    public void onListAvailableJobsDone(AsyncResult<List<String>> result)
    {
    }

    @Override
    public void onPerformJobDone(AsyncResult<AppJobCompletion> asyncResult)
    {
    }

}
