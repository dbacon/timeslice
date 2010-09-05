package com.enokinomi.timeslice.web.gwt.client.controller;

import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.beans.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.beans.StartTag;
import com.enokinomi.timeslice.web.gwt.client.beans.TaskTotal;
import com.enokinomi.timeslice.web.gwt.client.entry.AsyncResult;


public interface IControllerListener
{
    void serverInfoRecieved(String info);
    void authenticated();
    void unauthenticated(boolean retry);
    void onRefreshItemsDone(AsyncResult<List<StartTag>> result);
    void onAddItemDone(AsyncResult<Void> result);
    void onRefreshTotalsDone(AsyncResult<List<TaskTotal>> result);
    void onPersistTotalsDone(AsyncResult<String> result);
    void onRefreshTotalsAssignedDone(AsyncResult<List<AssignedTaskTotal>> result);
    void onAssignBilleeDone(AsyncResult<Void> result);
}
