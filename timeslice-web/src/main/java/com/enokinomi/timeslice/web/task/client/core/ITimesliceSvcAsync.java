package com.enokinomi.timeslice.web.task.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.ui.SortDir;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ITimesliceSvcAsync
{
    void getBrandInfo(AsyncCallback<BrandInfo> callback);

    // TODO: move these into a separate service, since others now also use it.
    void serverInfo(AsyncCallback<String> callback);
    void authenticate(String username, String password, AsyncCallback<String> callback);
    void logout(String authToken, AsyncCallback<Void> callback);

    void refreshItems(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, AsyncCallback<List<StartTag>> callback);
    void refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, AsyncCallback<List<TaskTotal>> callback);
    void addItem(String authToken, String instantString, String taskDescription, AsyncCallback<Void> callback);
    void addItems(String authToken, List<StartTag> items, AsyncCallback<Void> callback);
    void update(String authToken, StartTag editedStartTag, AsyncCallback<Void> callback);

}
