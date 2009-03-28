package bacond.timeslicer.web.gwt.client.entry;

import java.util.ArrayList;
import java.util.List;

import bacond.timeslicer.web.gwt.client.controller.Controller;
import bacond.timeslicer.web.gwt.client.entry.TimesliceApp.Defaults;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OptionsPanel extends Composite
{
	private final TextBox maxSize = new TextBox();
	private final TextBox baseUri = new TextBox();
	
	private final TextBox username = new TextBox();
	private final PasswordTextBox password = new PasswordTextBox();

	private final CheckBox controlSpaceSends = new CheckBox("Control-space also sends.");
	
	private final Controller controller;
	
	public static interface IOptionsListener
	{
		void optionsChanged(OptionsPanel source);
	}
	
	private final List<IOptionsListener> listeners = new ArrayList<IOptionsListener>();
	
	public void addOptionsListener(IOptionsListener listener)
	{
		if (null != listener)
		{
			listeners.add(listener);
		}
	}
	
	public void removeOptionsListener(IOptionsListener listener)
	{
		listeners.remove(listener);
	}
	
	protected void fireChanged()
	{
		for (IOptionsListener listener: listeners)
		{
			listener.optionsChanged(this);
		}
	}
	
	private String calculateServiceRoot()
	{
		String svcRoot = GWT.getHostPageBaseURL().substring(0, GWT.getHostPageBaseURL().indexOf("/" + GWT.getModuleName() + "/")); // + "/items/";
		return svcRoot;
	}
	
	private Label createTitledLabel(String text, String title)
	{
		Label l1 = new Label(text, false);
		l1.setTitle(title);
		return l1;
	}
	
	public OptionsPanel(final Controller controllerp)
	{
		this.controller = controllerp;
		
		localWidgetsInit();

		int row = 0;
		FlexTable optionsTable = new FlexTable();
		optionsTable.setWidget(row,   0, createTitledLabel("Base URI", "Service root is here, should be autodected."));
		optionsTable.setWidget(row++, 1, baseUri);
		optionsTable.setWidget(row,   0, createTitledLabel("Username", "You put this in the ACL on the service side."));
		optionsTable.setWidget(row++, 1, username);
		optionsTable.setWidget(row,   0, createTitledLabel("Password", "Your password."));
		optionsTable.setWidget(row++, 1, password);
		optionsTable.setWidget(row,   0, createTitledLabel("Max results", "Number of items to show in history and include in word-completion."));
		optionsTable.setWidget(row++, 1, maxSize);
		optionsTable.setWidget(row++, 0, controlSpaceSends);

		initWidget(optionsTable);
	}

	private void localWidgetsInit()
	{
		maxSize.setText("" + Defaults.MaxResults);
		maxSize.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget sender)
			{
				fireChanged();
			}
		});
		
		baseUri.setText(calculateServiceRoot());
		controller.getItemSvc().setBaseSvcUri(baseUri.getText());
		baseUri.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget sender)
			{
				controller.getItemSvc().setBaseSvcUri(baseUri.getText());
				fireChanged();
			}
		});
		
		username.setText(controller.getItemSvc().getUsername());
		username.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget arg0)
			{
				controller.getItemSvc().setUsername(username.getText());
				fireChanged();
			}
		});
		
		password.setText(controller.getItemSvc().getPassword());
		password.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget arg0)
			{
				controller.getItemSvc().setPassword(password.getText());
				fireChanged();
			}
		});
	}
	
	public int getMaxSize()
	{
		return Integer.valueOf(maxSize.getText());
	}
	
	public boolean isControlSpaceSends()
	{
		return controlSpaceSends.isChecked();
	}
}
