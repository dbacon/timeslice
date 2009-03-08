package bacond.timeslicer.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bacond.timeslicer.web.gwt.client.beans.StartTag;
import bacond.timeslicer.web.gwt.client.widget.TaskPanel.ITaskPanelListener;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HistoryPanel extends Composite 
{
	private final VerticalPanel itemsPanel = new VerticalPanel();
	private final ScrollPanel scroller = new ScrollPanel(itemsPanel);
	
	private final List<StartTag> items = new ArrayList<StartTag>();
	
	public static interface IHistoryPanelListener
	{
		void interestingThing(String p);
	}
	
	private final List<IHistoryPanelListener> listeners = new ArrayList<IHistoryPanelListener>();
	
	public void addHistoryPanelListener(IHistoryPanelListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeHistoryPanelListener(IHistoryPanelListener listener)
	{
		listeners.remove(listener);
	}
	
	protected void fireInterestingThing(String p)
	{
		for (IHistoryPanelListener listener: listeners)
		{
			listener.interestingThing(p);
		}
	}
	
	public HistoryPanel()
	{
		scroller.setSize("50em", "20em");
		initWidget(scroller);
	}
	
	public void clear(boolean shouldUpdate)
	{
		items.clear();
		
		if (shouldUpdate)
		{
			update();
		}
	}

	public void clear()
	{
		clear(true);
	}
	
	public void addItems(List<StartTag> items)
	{
		addItems(items, true);
	}

	public void addItems(List<StartTag> items, boolean shouldUpdate)
	{
		this.items.addAll(items);
	
		if (shouldUpdate)
		{
			update();
		}
	}
	
	public class TaskPanelListener implements ITaskPanelListener
	{
		public void resumeClicked(StartTag historicStartTag)
		{
			fireInterestingThing(historicStartTag.getDescription());
		}
	}

	final TaskPanelListener listener = new TaskPanelListener();

	protected void update()
	{
		Collections.reverse(items);
		
		itemsPanel.clear();
		for (StartTag item: items) 
		{
			TaskPanel taskPanel = new TaskPanel(item);
			taskPanel.addTaskPanelListener(listener);
			itemsPanel.add(taskPanel);
//			itemsPanel.add(new HTML(
//					"<div>" + item.getInstantString() + "</div>" +
//					"<div style=\"padding-left: 5em;\"><b>" + item.getDescription() + "</b>" +
//					"    <i><small>" +
//					"" + (null == item.getDurationMillis() ? "(on-going)": ("(" + (item.getDurationMillis() / 1000.0)) + " second(s))") +
//					"    </small></i>" +
//					"</div>" +
//					" " +
////					"<br/>" +
////					"<small><i>(" + item.getInstantString() + " ~ " + Transforms.mapNullTo(item.getUntilString(), "$") + ")</i></small>" +
////					" <large><b>" + item.getDescription() + "</b></large>" +
////					" (" + (null == item.getDurationMillis() ? "?": (item.getDurationMillis() / 1000.0)) + " second(s))" +
//					"", false));
		}
		
//		itemsPanel.add(entryPanel);
		
		scroller.scrollToBottom();
//		scroller.scrollToTop();
		scroller.scrollToRight();
	}
}
