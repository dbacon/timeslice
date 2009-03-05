package bacond.timeslicer.web.gwt.client.controller;

import java.util.ArrayList;
import java.util.List;

import bacond.timeslicer.web.gwt.client.beans.Item;
import bacond.timeslicer.web.gwt.client.entry.AsyncResult;
import bacond.timeslicer.web.gwt.client.server.IRequestEnder;
import bacond.timeslicer.web.gwt.client.server.ItemJsonSvc;

public class Controller
{
	private List<IControllerListener> listeners = new ArrayList<IControllerListener>();
	
	public void addControllerListener(IControllerListener listener)
	{
		if (listener != null)
		{
			listeners.add(listener);
		}
	}
	
	public void removeControllerListener(IControllerListener listener)
	{
		if (listener != null)
		{
			listeners.remove(listener);
		}
	}
	
	protected void fireRefreshItemsDone(AsyncResult<List<Item>> result)
	{
		for (IControllerListener listener: listeners)
		{
			listener.onRefreshItemsDone(result);
		}
	}
	
	protected void fireAddItemDone(AsyncResult<Void> result)
	{
		for (IControllerListener listener: listeners)
		{
			listener.onAddItemDone(result);
		}
	}

	private final ItemJsonSvc itemSvc = new ItemJsonSvc();
	
	public void startRefreshItems()
	{
		itemSvc.beginRefreshItems(
			new IRequestEnder<List<Item>>()
			{
				public void end(AsyncResult<List<Item>> result)
				{
					fireRefreshItemsDone(result);
				}
			});
	}
	
	public void startAddItem(String key, String project)
	{
		itemSvc.beginAddItem(key, project,
			new IRequestEnder<Void>()
			{
				public void end(AsyncResult<Void> result)
				{
					fireAddItemDone(result);
				}
			});
	}
}