package bacond.timeslicer.web.gwt.client.entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bacond.timeslicer.web.gwt.client.beans.StartTag;
import bacond.timeslicer.web.gwt.client.controller.Controller;
import bacond.timeslicer.web.gwt.client.controller.IControllerListener;
import bacond.timeslicer.web.gwt.client.widget.TaskPanel;
import bacond.timeslicer.web.gwt.client.widget.TaskPanel.ITaskPanelListener;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TimeSlicerApp implements EntryPoint
{

	public static final class Defaults
	{
		public static final String BaseUri = "http://localhost:8082";
		public static final int MaxResults = 10;
	}

	private final class TriggerRefreshCommand implements Command
	{
		public void execute()
		{
			triggerRefresh();
		}
	}

	public class TaskPanelListener implements ITaskPanelListener
	{
		public void resumeClicked(StartTag historicStartTag)
		{
			enterNewStartTag(historicStartTag.getDescription());
		}
	}

	private final VerticalPanel itemsPanel = new VerticalPanel();
	private final ScrollPanel scroller = new ScrollPanel(itemsPanel);
	private final TextBox taskDescriptionEntry = new TextBox();
	
	private final Controller controller = new Controller();
	private final HorizontalPanel entryPanel = new HorizontalPanel();
	
	private final TextBox maxSize = new TextBox();
	private final TextBox baseUri = new TextBox();
	private final DisclosurePanel optionsPanel = new DisclosurePanel("Options", false);
	
	public int getMaxSize()
	{
		return Integer.valueOf(maxSize.getText());
	}
	
	private void enterNewStartTag(String description)
	{
		if (description.trim().isEmpty())
		{
			triggerRefresh();
		}
		else
		{
			controller.startAddItem("", description);
		}
	}

	public void onModuleLoad()
	{
		taskDescriptionEntry.addKeyboardListener(new KeyboardListenerAdapter()
		{
			public void onKeyPress(Widget sender, char keyCode, int modifiers)
			{
				super.onKeyPress(sender, keyCode, modifiers);

				if (KEY_ENTER == keyCode)
				{
					enterNewStartTag(taskDescriptionEntry.getText());
				}
			}
		});
		
		maxSize.setText("" + Defaults.MaxResults);
		maxSize.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget sender)
			{
				triggerRefresh();
			}
		});
		
		baseUri.setText(Defaults.BaseUri);
		controller.setBaseSvcUri(baseUri.getText());
		baseUri.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget sender)
			{
				controller.setBaseSvcUri(baseUri.getText());
			}
		});

		
		VerticalPanel optionsVp = new VerticalPanel();
		optionsVp.setSpacing(5);
		optionsVp.add(maxSize);
		optionsVp.add(baseUri);
		
		optionsPanel.add(optionsVp);
		optionsPanel.setAnimationEnabled(true);

		entryPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		entryPanel.setSpacing(5);
		Hyperlink updateLink = new Hyperlink("[Update]", null);
		updateLink.addClickListener(new ClickListener()
		{
			public void onClick(Widget sender)
			{
				triggerRefresh();
			}
		});
		entryPanel.add(updateLink);
		entryPanel.add(new Label("Task: "));
		entryPanel.add(taskDescriptionEntry);
		
		scroller.setSize("50em", "20em");

		VerticalPanel vp = new VerticalPanel();
		vp.add(scroller);
		vp.add(entryPanel);
		vp.add(optionsPanel);
		
		DecoratedTabPanel tp = new DecoratedTabPanel();
		tp.add(vp, "Items");
		tp.selectTab(0);
		RootPanel.get().add(tp);
		
		controller.addControllerListener(new IControllerListener()
			{
				public void onAddItemDone(AsyncResult<Void> result)
				{
					handleAddItemDone(result);
				}
	
				public void onRefreshItemsDone(AsyncResult<List<StartTag>> result)
				{
					handleRefreshItemsDone(result);
				}
			});
		
		DeferredCommand.addCommand(new TriggerRefreshCommand());
	}
	
	final TaskPanelListener listener = new TaskPanelListener();

	private void handleRefreshItemsDone(AsyncResult<List<StartTag>> result)
	{
		if (!result.isError())
		{
			ArrayList<StartTag> items = new ArrayList<StartTag>(result.getReturned());
			
			Collections.reverse(items);
			
			itemsPanel.clear();
			for (StartTag item: items) 
			{
				TaskPanel taskPanel = new TaskPanel(item);
				taskPanel.addTaskPanelListener(listener);
				itemsPanel.add(taskPanel);
//				itemsPanel.add(new HTML(
//						"<div>" + item.getInstantString() + "</div>" +
//						"<div style=\"padding-left: 5em;\"><b>" + item.getDescription() + "</b>" +
//						"    <i><small>" +
//						"" + (null == item.getDurationMillis() ? "(on-going)": ("(" + (item.getDurationMillis() / 1000.0)) + " second(s))") +
//						"    </small></i>" +
//						"</div>" +
//						" " +
////						"<br/>" +
////						"<small><i>(" + item.getInstantString() + " ~ " + Transforms.mapNullTo(item.getUntilString(), "$") + ")</i></small>" +
////						" <large><b>" + item.getDescription() + "</b></large>" +
////						" (" + (null == item.getDurationMillis() ? "?": (item.getDurationMillis() / 1000.0)) + " second(s))" +
//						"", false));
			}
			
//			itemsPanel.add(entryPanel);
			
			scroller.scrollToBottom();
//			scroller.scrollToTop();
			scroller.scrollToRight();
		}
		else
		{
			showError(result);
			
//			messagePanel.add(new AcknowledgableMessagePanel("No refresh happened: " + result.getThrown().getMessage()));
		}
	}

	private void showError(AsyncResult<?> result)
	{
		DialogBox msgBox = new DialogBox(true);
		msgBox.setWidget(new Label(result.getThrown().getMessage()));
		msgBox.show();
		GWT.log("showed message: " + result.getStatus(), null);
	}

	private void handleAddItemDone(AsyncResult<Void> result)
	{
		if (!result.isError())
		{
//			messagePanel.add(new AcknowledgableMessagePanel("Item added."));
			triggerRefresh();
			taskDescriptionEntry.setText("");
		}
		else
		{
			showError(result);
//			messagePanel.add(new AcknowledgableMessagePanel("No item added."));
		}
		
//		newItemForm.setFormEnabled(true);
	}

	private void triggerRefresh()
	{
		controller.startRefreshItems(getMaxSize());
	}
}
