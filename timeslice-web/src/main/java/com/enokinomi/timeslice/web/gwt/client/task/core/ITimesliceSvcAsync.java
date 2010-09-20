package com.enokinomi.timeslice.web.gwt.client.task.core;

import java.util.List;


import com.enokinomi.timeslice.web.gwt.client.core.BrandInfo;
import com.enokinomi.timeslice.web.gwt.client.core.ProcType;
import com.enokinomi.timeslice.web.gwt.client.core.SortDir;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ITimesliceSvcAsync
{
    void serverInfo(AsyncCallback<String> callback);
    void authenticate(String user, String password, AsyncCallback<String> callback);
    void logout(String authToken, AsyncCallback<Void> callback);
    void refreshItems(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, AsyncCallback<List<StartTag>> callback);
    void refreshTotals(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, AsyncCallback<List<TaskTotal>> callback);
    void addItem(String authToken, String instantString, String taskDescription, AsyncCallback<Void> callback);
    void addItems(String authToken, List<StartTag> items, AsyncCallback<Void> callback);
    void update(String authToken, StartTag editedStartTag, AsyncCallback<Void> callback);
    void persistTotals(String authToken, String persistAsName, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords, AsyncCallback<String> callback);
    void getBrandInfo(AsyncCallback<BrandInfo> callback);
}
