package bacond.timeslicer.web.gwt.client.controller;

import java.util.List;

import bacond.timeslicer.web.gwt.client.beans.StartTag;
import bacond.timeslicer.web.gwt.client.entry.AsyncResult;

public interface IControllerListener
{
	void onRefreshItemsDone(AsyncResult<List<StartTag>> result);
	void onAddItemDone(AsyncResult<Void> result);
}