package bacond.timeslice.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ItemNew extends Composite
{
	private final TextBox keyTextBox = new TextBox();
	private final TextBox projectNameTextBox = new TextBox();
	private final Button submitButton = new Button("Add");
	private final HTML resultsHTML = new HTML();
	
	public static interface ItemNewListener
	{
		public void onSubmitted();
	}
	
	private final List<ItemNewListener> listeners = new ArrayList<ItemNewListener>();
	
	protected void fireOnSubmitted()
	{
		for (ItemNewListener listener: listeners)
		{
			listener.onSubmitted();
		}
	}
	
	public String getKeyText()
	{
		return keyTextBox.getText();
	}
	
	public String getProjectText()
	{
		return projectNameTextBox.getText();
	}
	
	public void addItemNewListener(ItemNewListener listener)
	{
		if (null != listener)
		{
			listeners.add(listener);
		}
	}

	public void removeItemNewListener(ItemNewListener listener)
	{
		if (null != listener)
		{
			listeners.remove(listener);
		}
	}

	public void setFormEnabled(boolean enabled)
	{
		keyTextBox.setEnabled(enabled);
		projectNameTextBox.setEnabled(enabled);
		submitButton.setEnabled(enabled);
	}
	
	public ItemNew()
	{
		FlexTable table = new FlexTable();

		keyTextBox.setName("key");
		table.setWidget(0, 0, new HTML("Item key", false));
		table.setWidget(0, 1, keyTextBox);

		projectNameTextBox.setName("project");
		table.setWidget(1, 0, new HTML("Project name", false));
		table.setWidget(1, 1, projectNameTextBox);

		submitButton.addClickListener(new ClickListener()
		{
			public void onClick(Widget sender)
			{
				fireOnSubmitted();
			}
		});
		
		resultsHTML.setHTML("");
		
		VerticalPanel vp = new VerticalPanel();
		vp.add(table);
		vp.add(submitButton);
		vp.add(resultsHTML);
		
		DecoratorPanel p = new DecoratorPanel();
		p.add(vp);
	
		initWidget(p);
	}
}
