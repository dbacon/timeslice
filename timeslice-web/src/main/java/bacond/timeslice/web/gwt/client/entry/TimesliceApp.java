package bacond.timeslice.web.gwt.client.entry;

import java.util.ArrayList;
import java.util.List;

import bacond.timeslice.web.gwt.client.beans.StartTag;
import bacond.timeslice.web.gwt.client.controller.Controller;
import bacond.timeslice.web.gwt.client.controller.IControllerListener;
import bacond.timeslice.web.gwt.client.widget.HistoryPanel;
import bacond.timeslice.web.gwt.client.widget.ReportPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TimesliceApp implements EntryPoint
{
	public static final String IssuesUrl = "http://code.google.com/p/timeslice/issues/list";
	private static final String FormsUrl = "forms/items-new.html";

	public static final class Defaults
	{
		public static final String BaseUri = "http://localhost:8082";
		public static final int MaxResults = 10;
	}

	private final Controller controller = new Controller();
	
	private final OptionsPanel optionsPanel = new OptionsPanel(controller);

	private final HistoryPanel historyPanel = new HistoryPanel();
	private final MultiWordSuggestOracle suggestSource = new MultiWordSuggestOracle();
	
	private final SuggestBox taskDescriptionEntry = new SuggestBox(suggestSource);
	
	private final HorizontalPanel entryPanel = new HorizontalPanel();
	
	private final Button updateButton = new Button("Update");
	
	private void updateStartTag(StartTag editedStartTag)
	{
		controller.startEditDescription(editedStartTag);
	}

	private void enterNewStartTag(String instantString, String description)
	{
		if (description.trim().isEmpty())
		{
			scheduleRefresh();
		}
		else
		{
			controller.startAddItem(instantString, description);
		}
	}

	public void onModuleLoad()
	{
		optionsPanel.addOptionsListener(new OptionsPanel.IOptionsListener()
		{
			public void optionsChanged(OptionsPanel source)
			{
				scheduleRefresh();
			}
		});
		
		historyPanel.addHistoryPanelListener(new HistoryPanel.IHistoryPanelListener()
		{
			public void interestingThing(String p)
			{
				enterNewStartTag("", p);
			}

			public void fireEdited(StartTag editedStartTag)
			{
				updateStartTag(editedStartTag);
			}

			public void fireTimeEdited(StartTag startTag)
			{
				enterNewStartTag(startTag.getInstantString(), startTag.getDescription());
			}
		});
		
		taskDescriptionEntry.setWidth("30em");
		taskDescriptionEntry.addKeyboardListener(new KeyboardListenerAdapter()
		{
			public void onKeyPress(Widget sender, char keyCode, int modifiers)
			{
				
				if (0 != (modifiers & MODIFIER_CTRL)
						&& (keyCode == KEY_ENTER || (keyCode == ' ' && optionsPanel.isControlSpaceSends())))
				{
					enterNewStartTag("", taskDescriptionEntry.getText());
				}
				
				super.onKeyPress(sender, keyCode, modifiers);
			}
		});
		
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
		
		historyPanel.setHeight("20em");
		historyPanel.setWidth("50em");
		
		ReportPanel p2 = new ReportPanel(controller);
		
		final DecoratedTabPanel tp = new DecoratedTabPanel();
		tp.add(vp, "Input");
		tp.add(p2, "Reports");
		tp.add(optionsPanel, "Options");
		tp.selectTab(0);
		tp.setAnimationEnabled(true);
		
		HorizontalPanel buildLabelBox = new HorizontalPanel();
		buildLabelBox.setSpacing(15);
		buildLabelBox.add(new HTML("<a href=\"" + IssuesUrl + "\" target=\"_blank\">Feedback / RFEs / Bugs</a>"));
		buildLabelBox.add(new HTML("<a href=\"" + FormsUrl + "\" target=\"_blank\">Input Forms</a>"));

		final DockPanel dockPanel = new DockPanel();
		dockPanel.setSpacing(5);
		dockPanel.add(tp, DockPanel.CENTER);
		dockPanel.add(buildLabelBox, DockPanel.SOUTH);
		
		RootPanel.get().add(dockPanel);
		
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
				controller.startRefreshItems(optionsPanel.getMaxSize());
			}
		});
	}
}
