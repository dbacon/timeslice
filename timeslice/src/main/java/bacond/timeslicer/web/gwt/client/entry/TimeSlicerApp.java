package bacond.timeslicer.web.gwt.client.entry;

import java.util.List;

import bacond.timeslicer.web.gwt.client.beans.Item;
import bacond.timeslicer.web.gwt.client.controller.Controller;
import bacond.timeslicer.web.gwt.client.controller.IControllerListener;
import bacond.timeslicer.web.gwt.client.widget.AcknowledgableMessagePanel;
import bacond.timeslicer.web.gwt.client.widget.ItemNew;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TimeSlicerApp implements EntryPoint
{
	private final ItemNew newItemForm = new ItemNew();
	private final VerticalPanel itemsPanel = new VerticalPanel();
	private final VerticalPanel messagePanel = new VerticalPanel();
	private final DisclosurePanel errorPanel = new DisclosurePanel("Messages");
	
	private final Controller controller = new Controller();

	public void onModuleLoad()
	{
		newItemForm.addItemNewListener(new ItemNew.ItemNewListener()
			{
				public void onSubmitted()
				{
					newItemForm.setFormEnabled(false);
					controller.startAddItem(newItemForm.getKeyText(), newItemForm.getProjectText());
				}
			});
		
		errorPanel.add(messagePanel);

		VerticalPanel vp = new VerticalPanel();
		vp.add(newItemForm);
		vp.add(itemsPanel);
		DecoratorPanel p = new DecoratorPanel();
		p.add(errorPanel);
		vp.add(p);

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
	
				public void onRefreshItemsDone(AsyncResult<List<Item>> result)
				{
					handleRefreshItemsDone(result);
				}
			});
		
		DeferredCommand.addCommand(new Command()
			{
				public void execute()
				{
					controller.startRefreshItems();
				}
			});
	}

	private void handleRefreshItemsDone(AsyncResult<List<Item>> result)
	{
		if (!result.isError())
		{
			itemsPanel.clear();
			for (Item item: result.getReturned())
			{
				itemsPanel.add(new HTML("<small><i>(" + item.getKey() + ")</i></small> <large><b>" + item.getProject() + "</b></large>", false));
			}
			
			messagePanel.add(new AcknowledgableMessagePanel("Refreshed items."));
		}
		else
		{
			messagePanel.add(new AcknowledgableMessagePanel("No refresh happened."));
		}
	}

	private void handleAddItemDone(AsyncResult<Void> result)
	{
		if (!result.isError())
		{
			messagePanel.add(new AcknowledgableMessagePanel("Item added."));
			controller.startRefreshItems();
		}
		else
		{
			messagePanel.add(new AcknowledgableMessagePanel("No item added."));
		}

		newItemForm.setFormEnabled(true);
	}
}
