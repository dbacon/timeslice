package bacond.timeslicer.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.List;

import bacond.timeslicer.web.gwt.client.beans.StartTag;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TaskPanel extends Composite
{
	public static interface ITaskPanelListener
	{
		void resumeClicked(StartTag historicStartTag);
	}
	
	private final List<ITaskPanelListener> listeners = new ArrayList<ITaskPanelListener>();
	
	public void addTaskPanelListener(ITaskPanelListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeTaskPanelListener(ITaskPanelListener listener)
	{
		listeners.remove(listener);
	}
	
	protected void fireResumeClicked(StartTag startTag)
	{
		for (ITaskPanelListener listener: listeners)
		{
			listener.resumeClicked(startTag);
		}
	}

	public TaskPanel(final StartTag startTag)
	{
		Hyperlink resumeLink = new Hyperlink("[R]", null);
		resumeLink.setTitle("Resume this task");
		resumeLink.addClickListener(new ClickListener()
		{
			public void onClick(Widget sender)
			{
				fireResumeClicked(startTag);
			}
		});
		
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.setSpacing(5);
		hp1.add(resumeLink);
		hp1.add(new Label(startTag.getDescription()));
		if (null != startTag.getUntilString())
		{
			hp1.add(new Label("" + (startTag.getDurationMillis() / 1000.0) + " second(s)"));
		}
		
		VerticalPanel vp = new VerticalPanel();
		vp.add(new Label(startTag.getInstantString()));
		vp.add(hp1);
		
		initWidget(vp);
	}
}
