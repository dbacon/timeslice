package bacond.timeslice.web.gwt.client.controller;

import java.util.List;

import bacond.timeslice.web.gwt.client.beans.StartTag;
import bacond.timeslice.web.gwt.client.server.ProcType;
import bacond.timeslice.web.gwt.client.server.SortDir;

public interface IController
{

	void addControllerListener(IControllerListener listener);
	void removeControllerListener(IControllerListener listener);

	void logout();

    void serverInfo();
	void startEditDescription(StartTag editedStartTag);
	void startAddItem(String instantString, String taskDescription);
	void startRefreshItems(int maxSize);
	void startRefreshTotals(int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
	void startPersistTotals(String persistAsName, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);

}
