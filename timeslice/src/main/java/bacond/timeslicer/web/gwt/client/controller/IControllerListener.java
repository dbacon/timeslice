package bacond.timeslicer.web.gwt.client.controller;

import java.util.List;

import bacond.timeslicer.web.gwt.client.beans.Item;
import bacond.timeslicer.web.gwt.client.entry.AsyncResult;

public interface IControllerListener
{
	void onRefreshItemsDone(AsyncResult<List<Item>> result);
	void onAddItemDone(AsyncResult<Void> result);
}