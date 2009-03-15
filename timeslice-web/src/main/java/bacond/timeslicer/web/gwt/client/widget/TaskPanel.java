package bacond.timeslicer.web.gwt.client.widget;

import static bacond.timeslicer.web.gwt.client.widget.HumanReadableTimeHelper.formatDuration;

import java.util.ArrayList;
import java.util.List;

import bacond.timeslicer.web.gwt.client.beans.StartTag;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListenerAdapter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TaskPanel extends Composite
{
	public static interface ITaskPanelListener
	{
		void resumeClicked(StartTag historicStartTag);
		void itemEdited(StartTag editedTag);
	}
	
	private final List<ITaskPanelListener> listeners = new ArrayList<ITaskPanelListener>();
	private final HorizontalPanel descriptionContainer= new HorizontalPanel();
	private final Label label = new Label();
	private final TextBox descriptionEditor = new TextBox();
	
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
	
	protected void fireEdited(StartTag startTag)
	{
		for (ITaskPanelListener listener: listeners)
		{
			listener.itemEdited(startTag);
		}
	}
	
	private void editModeOn(final StartTag startTag)
	{
		label.setVisible(false);
		descriptionEditor.setText(startTag.getDescription());
		descriptionEditor.setVisible(true);
		descriptionEditor.selectAll();
		descriptionEditor.setFocus(true);
	}

	private void editModeOff(final StartTag startTag)
	{
		descriptionEditor.setVisible(false);
		label.setText(descriptionEditor.getText());
		label.setVisible(true);
		
		if (null != startTag)
		{
			fireEdited(new StartTag(
					startTag.getInstantString(),
					startTag.getUntilString(),
					startTag.getDurationMillis(),
					descriptionEditor.getText().trim()));
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
		
		descriptionEditor.addKeyboardListener(new KeyboardListenerAdapter()
		{
			public void onKeyPress(Widget sender, char keyCode, int modifiers)
			{
				if ((modifiers & MODIFIER_CTRL) != 0 && keyCode == KEY_ENTER)
				{
					editModeOff(startTag);
				}

				super.onKeyPress(sender, keyCode, modifiers);
			}

		});
		
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.setSpacing(5);
		hp1.add(resumeLink);
		label.setText(startTag.getDescription());
		label.addClickListener(new ClickListener()
		{
			public void onClick(Widget arg0)
			{
				editModeOn(startTag);
			}
		});
		
		String descWidth = "30em";
		
		label.setWidth(descWidth);
		descriptionContainer.add(label);
		descriptionContainer.add(descriptionEditor);
		descriptionEditor.setVisible(false);
		descriptionEditor.setWidth(descWidth);
		descriptionEditor.addFocusListener(new FocusListenerAdapter()
		{
			public void onLostFocus(Widget sender)
			{
				editModeOff(null);
				super.onLostFocus(sender);
			}
		});
		hp1.add(descriptionContainer);
		if (null != startTag.getUntilString())
		{
			hp1.add(new Label(formatDuration(startTag.getDurationMillis().longValue())));
		}
		
		VerticalPanel vp = new VerticalPanel();
//		vp.add(new Label(startTag.getInstantString()));
		vp.add(hp1);
		
		initWidget(vp);
	}
}
