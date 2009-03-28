package bacond.timeslice.web.gwt.client.widget;

import static bacond.timeslice.web.gwt.client.widget.HumanReadableTimeHelper.formatDuration;

import java.util.ArrayList;
import java.util.List;

import bacond.timeslice.web.gwt.client.beans.StartTag;

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
		void timeEdited(StartTag newTag);
	}
	
	private final List<ITaskPanelListener> listeners = new ArrayList<ITaskPanelListener>();
	private final HorizontalPanel descriptionContainer= new HorizontalPanel();
	private final Label label = new Label();
	private final TextBox descriptionEditor = new TextBox();
	private final HorizontalPanel timeContainer = new HorizontalPanel();
	private final Label timeLabel = new Label();
	private final TextBox timeEditor = new TextBox();
	private boolean losingFocusAccepts = false;

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
	
	protected void fireTimeEdited(StartTag startTag)
	{
		for (ITaskPanelListener listener: listeners)
		{
			listener.timeEdited(startTag);
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

	private void editModeOn2(final StartTag startTag)
	{
		timeLabel.setVisible(false);
		timeEditor.setText(startTag.getInstantString());
		timeEditor.setVisible(true);
		timeEditor.selectAll();
		timeEditor.setFocus(true);
	}

	private void editModeOff(final StartTag startTag, boolean accepted)
	{
		descriptionEditor.setVisible(false);
		label.setText(startTag.getDescription());
		label.setVisible(true);
		
		if (accepted)
		{
			startTag.setDescription(descriptionEditor.getText());
			label.setText(descriptionEditor.getText());
			fireEdited(new StartTag(
					startTag.getInstantString(),
					startTag.getUntilString(),
					startTag.getDurationMillis(),
					descriptionEditor.getText().trim()));
		}
	}

	private void editModeOff2(final StartTag startTag, boolean accepted)
	{
		timeEditor.setVisible(false);
		timeLabel.setText(formatDuration(startTag.getDurationMillis().longValue()));
		timeLabel.setVisible(true);
		
		if (accepted)
		{
			startTag.setInstantString(timeEditor.getText());
			timeLabel.setText(formatDuration(startTag.getDurationMillis().longValue()));
			fireTimeEdited(new StartTag(
				timeEditor.getText(),
				startTag.getUntilString(),
				startTag.getDurationMillis(),
				startTag.getDescription()));
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
				if (keyCode == KEY_ENTER)
				{
					editModeOff(startTag, true);
				}
				else if (keyCode == KEY_ESCAPE)
				{
					editModeOff(startTag, false);
				}
				else
				{
					super.onKeyPress(sender, keyCode, modifiers);
				}
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
		String timeWidth = "15em";
		
		label.setWidth(descWidth);
		descriptionContainer.add(label);
		descriptionContainer.add(descriptionEditor);
		descriptionEditor.setVisible(false);
		descriptionEditor.setWidth(descWidth);
		descriptionEditor.addFocusListener(new FocusListenerAdapter()
		{
			public void onLostFocus(Widget sender)
			{
				if (descriptionEditor.isVisible())
				{
					editModeOff(startTag, losingFocusAccepts);
				}
				super.onLostFocus(sender);
			}
		});
		hp1.add(descriptionContainer);
		
		timeLabel.setWidth("10em");
		timeLabel.addClickListener(new ClickListener()
		{
			public void onClick(Widget arg0)
			{
				editModeOn2(startTag);
			}
		});
		timeContainer.add(timeLabel);
		timeContainer.add(timeEditor);
		timeEditor.setVisible(false);
		timeEditor.setWidth(timeWidth);
		timeEditor.addFocusListener(new FocusListenerAdapter()
		{
			public void onLostFocus(Widget sender)
			{
				if (timeEditor.isVisible())
				{
					editModeOff2(startTag, losingFocusAccepts);
				}
			
				super.onLostFocus(sender);
			}
		});
		timeEditor.addKeyboardListener(new KeyboardListenerAdapter()
		{
			public void onKeyPress(Widget sender, char keyCode, int modifiers)
			{
				if (keyCode == KEY_ENTER)
				{
					editModeOff2(startTag, true);
				}
				else if (keyCode == KEY_ESCAPE)
				{
					editModeOff2(startTag, false);
				}
				else
				{
					super.onKeyPress(sender, keyCode, modifiers);
				}
			}

		});
		if (null != startTag.getUntilString())
		{
			timeLabel.setText(formatDuration(startTag.getDurationMillis().longValue()));
			// new Label()
			hp1.add(timeContainer); 
		}
		
		VerticalPanel vp = new VerticalPanel();
//		vp.add(new Label(startTag.getInstantString()));
		vp.add(hp1);
		
		initWidget(vp);
	}
}
