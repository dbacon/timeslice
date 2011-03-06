package com.enokinomi.timeslice.web.task.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ITaskSvcAsync
{
    // tasks
    void refreshItems(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, AsyncCallback<List<StartTag>> callback);
    void addItem(String authToken, String instantString, String taskDescription, AsyncCallback<Void> callback);
    void addItems(String authToken, List<StartTag> items, AsyncCallback<Void> callback);
    void update(String authToken, StartTag editedStartTag, AsyncCallback<Void> callback);
    void mergeBack(String authToken, StartTag startTag, boolean multi, AsyncCallback<Void> callback);
    void removeItem(String authToken, StartTag startTag, AsyncCallback<Void> withRetry);

}
