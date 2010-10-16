package com.enokinomi.timeslice.web.task.client.core;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.NotAuthenticException;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.SortDir;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface ITimesliceSvc extends RemoteService
{
    BrandInfo getBrandInfo();

    // TODO: move these into a separate service, since others now also use it.
    String serverInfo();
    String authenticate(String username, String password);
    void logout(String authToken) throws NotAuthenticException;

    List<StartTag> refreshItems(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant) throws NotAuthenticException;
    List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords) throws NotAuthenticException;
    void addItem(String authToken, String instantString, String taskDescription) throws NotAuthenticException;
    void addItems(String authToken, List<StartTag> items) throws NotAuthenticException;
    void update(String authToken, StartTag editedStartTag) throws NotAuthenticException;

    String persistTotals(String authToken, String persistAsName, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords) throws NotAuthenticException;
}
