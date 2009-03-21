package bacond.timeslicer.web.gwt.client.entry;

import java.util.ArrayList;
import java.util.List;

import bacond.timeslicer.web.gwt.client.beans.StartTag;
import bacond.timeslicer.web.gwt.client.controller.Controller;
import bacond.timeslicer.web.gwt.client.controller.IControllerListener;
import bacond.timeslicer.web.gwt.client.widget.HistoryPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TimeSlicerApp implements EntryPoint
{
	public static final String IssuesUrl = "http://code.google.com/p/timeslice/issues/list";

	public static final class Defaults
	{
		public static final String BaseUri = "http://localhost:8082";
		public static final int MaxResults = 10;
	}

	private final HistoryPanel historyPanel = new HistoryPanel();
	private final MultiWordSuggestOracle suggestSource = new MultiWordSuggestOracle();
	
	private final SuggestBox taskDescriptionEntry = new SuggestBox(suggestSource);
	
	private final Controller controller = new Controller();
	private final HorizontalPanel entryPanel = new HorizontalPanel();
	
	private final TextBox maxSize = new TextBox();
	private final TextBox baseUri = new TextBox();
	private final DisclosurePanel optionsPanel = new DisclosurePanel("Options", false);
	
	private final DisclosurePanel manualTimeDisclosure = new DisclosurePanel("Manual Input", false);
	private final TextBox manualTimeEntry = new TextBox();
	private final Button updateButton = new Button("Update");
	
	private final CheckBox controlSpaceSends = new CheckBox("Control-space also sends.");
	
	private final TextBox username = new TextBox();
	private final PasswordTextBox password = new PasswordTextBox();

	public int getMaxSize()
	{
		return Integer.valueOf(maxSize.getText());
	}
	
	private void updateStartTag(StartTag editedStartTag)
	{
		controller.startEditDescription(editedStartTag);
	}

	private void enterNewStartTag(String description)
	{
		if (description.trim().isEmpty())
		{
			scheduleRefresh();
		}
		else
		{
			controller.startAddItem("", description);
		}
	}

	public void onModuleLoad()
	{
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.add(new Label("When:"));
		hp1.add(manualTimeEntry);
		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(hp1);
		manualTimeDisclosure.add(vp1);
		manualTimeDisclosure.setAnimationEnabled(true);
		
		historyPanel.addHistoryPanelListener(new HistoryPanel.IHistoryPanelListener()
		{
			public void interestingThing(String p)
			{
				enterNewStartTag(p);
			}

			public void fireEdited(StartTag editedStartTag)
			{
				updateStartTag(editedStartTag);
			}
		});
		
		taskDescriptionEntry.setWidth("30em");
		taskDescriptionEntry.addKeyboardListener(new KeyboardListenerAdapter()
		{
			public void onKeyPress(Widget sender, char keyCode, int modifiers)
			{
				
				if (0 != (modifiers & MODIFIER_CTRL)
						&& (keyCode == KEY_ENTER || (keyCode == ' ' && controlSpaceSends.isChecked())))
				{
					enterNewStartTag(taskDescriptionEntry.getText());
				}
				
				super.onKeyPress(sender, keyCode, modifiers);
			}
		});
		
		maxSize.setText("" + Defaults.MaxResults);
		maxSize.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget sender)
			{
				scheduleRefresh();
			}
		});
		
		baseUri.setText(calculateServiceRoot());
		controller.getItemSvc().setBaseSvcUri(baseUri.getText());
		baseUri.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget sender)
			{
				controller.getItemSvc().setBaseSvcUri(baseUri.getText());
				scheduleRefresh();
			}
		});
		
		username.setText(controller.getItemSvc().getUsername());
		username.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget arg0)
			{
				controller.getItemSvc().setUsername(username.getText());
				scheduleRefresh();
			}
		});
		
		password.setText(controller.getItemSvc().getPassword());
		password.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget arg0)
			{
				controller.getItemSvc().setPassword(password.getText());
				scheduleRefresh();
			}
		});

		int row = 0;
		FlexTable optionsTable = new FlexTable();
		optionsTable.setText  (row,   0, "Base URI");
		optionsTable.setWidget(row++, 1, baseUri);
		optionsTable.setText  (row,   0, "Username");
		optionsTable.setWidget(row++, 1, username);
		optionsTable.setText  (row,   0, "Password");
		optionsTable.setWidget(row++, 1, password);
		optionsTable.setText  (row,   0, "Max results");
		optionsTable.setWidget(row++, 1, maxSize);
		optionsTable.setWidget(row++, 0, controlSpaceSends);
		
		optionsPanel.add(optionsTable);
		optionsPanel.setAnimationEnabled(true);

		entryPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		entryPanel.setSpacing(5);
		updateButton.addClickListener(new ClickListener()
		{
			public void onClick(Widget sender)
			{
				scheduleRefresh();
			}
		});
		entryPanel.add(updateButton);
		entryPanel.add(new Label("Task: "));
		entryPanel.add(taskDescriptionEntry);
		
		VerticalPanel vp = new VerticalPanel();
		vp.add(historyPanel);
		vp.add(entryPanel);
		vp.add(manualTimeDisclosure);
		vp.add(optionsPanel);
		
		DecoratedTabPanel tp = new DecoratedTabPanel();
		tp.add(vp, "Items");
		tp.selectTab(0);
		VerticalPanel mainVp = new VerticalPanel();
		mainVp.setSpacing(5);
		mainVp.add(tp);
		HorizontalPanel buildLabelBox = new HorizontalPanel();
		Hyperlink feedbackLink = new Hyperlink("Feedback/RFEs/Bugs", null);
		feedbackLink.addClickListener(new ClickListener()
		{
			public void onClick(Widget arg0)
			{
				Window.open(IssuesUrl, "_blank", "");
			}
		});
		buildLabelBox.add(feedbackLink);
		mainVp.add(buildLabelBox);
		RootPanel.get().add(mainVp);
		
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
		
		scheduleRefresh();
	}

	private String calculateServiceRoot()
	{
		String svcRoot = GWT.getHostPageBaseURL().substring(0, GWT.getHostPageBaseURL().indexOf("/" + GWT.getModuleName() + "/")); // + "/items/";
		return svcRoot;
	}
	
	private void handleRefreshItemsDone(AsyncResult<List<StartTag>> result)
	{
		if (!result.isError())
		{
			ArrayList<StartTag> items = new ArrayList<StartTag>(result.getReturned());			
			
			historyPanel.clear(false);
			historyPanel.addItems(items);
			
			updateSuggestSource(items);
		}
		else
		{
			showError(result);
			
//			messagePanel.add(new AcknowledgableMessagePanel("No refresh happened: " + result.getThrown().getMessage()));
		}
	}

	private void updateSuggestSource(ArrayList<StartTag> items)
	{
		suggestSource.clear();
		for (StartTag tag: items)
		{
			suggestSource.add(tag.getDescription());
		}
	}

	private void showError(AsyncResult<?> result)
	{
		Label label = new Label(result.getThrown().getMessage());
		
		Label msgText = new Label(result.getStatus().toString());
		
		VerticalPanel vp = new VerticalPanel();
		vp.add(label);
		vp.add(msgText);

		DialogBox msgBox = new DialogBox(true);
		msgBox.setWidget(vp);
		msgBox.show();
		
		GWT.log("showed message: " + result.getStatus(), null);
	}

	private void handleAddItemDone(AsyncResult<Void> result)
	{
		if (!result.isError())
		{
//			messagePanel.add(new AcknowledgableMessagePanel("Item added."));
			taskDescriptionEntry.setText("");
			scheduleRefresh();
		}
		else
		{
			showError(result);
//			messagePanel.add(new AcknowledgableMessagePanel("No item added."));
		}
		
//		newItemForm.setFormEnabled(true);
	}

	private void scheduleRefresh()
	{
		DeferredCommand.addCommand(new Command()
		{
			public void execute()
			{
				controller.startRefreshItems(getMaxSize());
			}
		});
	}
}
