package bacond.timeslice.web.gwt.client.controller;

import java.util.List;

import bacond.timeslice.web.gwt.client.beans.StartTag;
import bacond.timeslice.web.gwt.client.entry.AsyncResult;

public interface IControllerListener
{
	void onRefreshItemsDone(AsyncResult<List<StartTag>> result);
	void onAddItemDone(AsyncResult<Void> result);
}
