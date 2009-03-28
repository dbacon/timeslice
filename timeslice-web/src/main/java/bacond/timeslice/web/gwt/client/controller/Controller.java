package bacond.timeslice.web.gwt.client.controller;

import java.util.ArrayList;
import java.util.List;

import bacond.timeslice.web.gwt.client.beans.StartTag;
import bacond.timeslice.web.gwt.client.entry.AsyncResult;
import bacond.timeslice.web.gwt.client.server.IRequestEnder;
import bacond.timeslice.web.gwt.client.server.ItemJsonSvc;

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
	
	protected void fireRefreshItemsDone(AsyncResult<List<StartTag>> result)
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
	
	public ItemJsonSvc getItemSvc()
	{
		return itemSvc;
	}
	
	public void startRefreshItems(int maxSize)
	{
		itemSvc.beginRefreshItems(maxSize,
			new IRequestEnder<List<StartTag>>()
			{
				public void end(AsyncResult<List<StartTag>> result)
				{
					fireRefreshItemsDone(result);
				}
			});
	}
	
	public void startAddItem(String instantString, String taskDescription)
	{
		itemSvc.beginAddItem(instantString, taskDescription,
			new IRequestEnder<Void>()
			{
				public void end(AsyncResult<Void> result)
				{
					fireAddItemDone(result);
				}
			});
	}

	public void startEditDescription(StartTag editedStartTag)
	{
		itemSvc.beginUpdate(editedStartTag,
				new IRequestEnder<Void>()
				{
					public void end(AsyncResult<Void> result)
					{
						fireAddItemDone(result); // TODO: trigger a related update (more specific)
					}
				});
	}
}