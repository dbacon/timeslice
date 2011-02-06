package com.enokinomi.timeslice.web.task.client.controller.impl;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.util.AsyncResult;
import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.enokinomi.timeslice.web.task.client.controller.api.IControllerListener;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;


/**
 * Provides listener management part of IController.
 *
 */
public abstract class BaseController implements IController
{
    private List<IControllerListener> listeners = new ArrayList<IControllerListener>();

    protected BaseController()
    {
    }

    public void addControllerListener(IControllerListener listener)
    {
        if (listener != null)
        {
            listeners.add(listener);
        }
    }

    public void removeControllerListener(IControllerListener listener)
    {
        if (listener != null)
        {
            listeners.remove(listener);
        }
    }

    protected void fireServerInfoRecieved(String info)
    {
        for (IControllerListener listener: listeners)
        {
            listener.serverInfoRecieved(info);
        }
    }

    protected void fireRefreshItemsDone(AsyncResult<List<StartTag>> result)
    {
        for (IControllerListener listener: listeners)
        {
            listener.onRefreshItemsDone(result);
        }
    }

    protected void fireRefreshTotalsDone(AsyncResult<List<TaskTotal>> result)
    {
        for (IControllerListener listener: listeners)
        {
            listener.onRefreshTotalsDone(result);
        }
    }

    protected void fireRefreshTotalsAssignedDone(AsyncResult<List<AssignedTaskTotal>> result)
    {
        for (IControllerListener listener: listeners)
        {
            listener.onRefreshTotalsAssignedDone(result);
        }
    }

    protected void fireAddItemDone(AsyncResult<Void> result)
    {
        for (IControllerListener listener: listeners)
        {
            listener.onAddItemDone(result);
        }
    }

    protected void fireAssignBilleeDone(AsyncResult<Void> result)
    {
        for (IControllerListener listener: listeners)
        {
            listener.onAssignBilleeDone(result);
        }
    }

    protected void fireListAvailableJobsDone(AsyncResult<List<String>> result)
    {
        for (IControllerListener listener: listeners)
        {
            listener.onListAvailableJobsDone(result);
        }
    }

    protected void firePerformJobDone(AsyncResult<AppJobCompletion> asyncResult)
    {
        for (IControllerListener listener: listeners)
        {
            listener.onPerformJobDone(asyncResult);
        }
    }

    protected void fireBranded(AsyncResult<BrandInfo> result)
    {
        for (IControllerListener listener: listeners)
        {
            listener.onBranded(result);
        }
    }

    protected void fireAllBilleesDone(AsyncResult<List<String>> asyncResult)
    {
        for (IControllerListener listener: listeners)
        {
            listener.onAllBilleesDone(asyncResult);
        }
    }

}
