package bacond.timeslice.web.gwt.client.server;

import java.util.List;

import bacond.timeslice.web.gwt.client.beans.NotAuthenticException;
import bacond.timeslice.web.gwt.client.beans.StartTag;
import bacond.timeslice.web.gwt.client.beans.TaskTotal;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("timeslice")
public interface ITimesliceSvc extends RemoteService
{
    String serverInfo();
    String authenticate(String username, String password);
    void logout(String authToken) throws NotAuthenticException;
	List<StartTag> refreshItems(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant) throws NotAuthenticException;
	List<TaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords) throws NotAuthenticException;
	void addItem(String authToken, String instantString, String taskDescription) throws NotAuthenticException;
	void addItems(String authToken, List<StartTag> items);
	void update(String authToken, StartTag editedStartTag) throws NotAuthenticException;

	String persistTotals(String authToken, String persistAsName, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords) throws NotAuthenticException;
}
