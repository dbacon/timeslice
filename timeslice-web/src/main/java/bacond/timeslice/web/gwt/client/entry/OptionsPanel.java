package bacond.timeslice.web.gwt.client.entry;

import java.util.ArrayList;
import java.util.List;

import bacond.timeslice.web.gwt.client.controller.Controller;
import bacond.timeslice.web.gwt.client.entry.TimesliceApp.Defaults;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OptionsPanel extends Composite
{
	public static class PrefKeys
	{
		public static final String PageSize = "timeslice.options.pagesize";
		public static final String User = "timeslice.options.user";
		public static final String CtrlSpaceSends = "timeslice.options.controlspacesends";
		public static final String CurrentTaskInTitlebar = "timeslice.options.currenttaskintitlebar";
		public static final String TitlebarTemplate= "timeslice.options.titlebartemplate";
	}

	public static class Token
	{
		public static String CurrentTask = "@current.task@";
	}

	public static final String DefaultTitlebarTemplate = "[TS] " + Token.CurrentTask;

	private final TextBox maxSize = new TextBox();
	private final TextBox baseUri = new TextBox();
	
	private final TextBox username = new TextBox();
	private final PasswordTextBox password = new PasswordTextBox();

	private final CheckBox controlSpaceSends = new CheckBox("Control-space also sends.");
	private final CheckBox currentTaskInTitlebar = new CheckBox("Show current task in page title.");
	private final TextBox titleBarTemplate = new TextBox();
	
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
		optionsTable.setWidget(row,   0, currentTaskInTitlebar);
		optionsTable.setWidget(row++, 1, titleBarTemplate);
		
		addOptionsListener(new IOptionsListener()
		{
			public void optionsChanged(OptionsPanel source)
			{
				writePrefs();
			}
		});

		initWidget(optionsTable);
	}

	private ClickListener CommonClickFireChanged = new ClickListener()
	{
		public void onClick(Widget arg0)
		{
			fireChanged();
		}
	};

	private ChangeListener CommonChangeFireChanged = new ChangeListener()
	{
		public void onChange(Widget sender)
		{
			fireChanged();
		}
	};

	private void localWidgetsInit()
	{
		maxSize.addChangeListener(CommonChangeFireChanged);
		
		baseUri.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget sender)
			{
				controller.getItemSvc().setBaseSvcUri(baseUri.getText());
				fireChanged();
			}
		});
		
		username.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget arg0)
			{
				controller.getItemSvc().setUsername(username.getText());
				fireChanged();
			}
		});
		
		password.addChangeListener(new ChangeListener()
		{
			public void onChange(Widget arg0)
			{
				controller.getItemSvc().setPassword(password.getText());
				fireChanged();
			}
		});
		
		controlSpaceSends.addClickListener(CommonClickFireChanged);

		titleBarTemplate.addChangeListener(CommonChangeFireChanged);

		currentTaskInTitlebar.addClickListener(CommonClickFireChanged);
		currentTaskInTitlebar.addClickListener(new ClickListener()
		{
			public void onClick(Widget arg0)
			{
				consistentize();
			}
		});

		initValues();

		consistentize();
	}

	private void consistentize()
	{
		titleBarTemplate.setEnabled(currentTaskInTitlebar.isChecked());
	}

	public int getMaxSize()
	{
		try
		{
			return Integer.valueOf(maxSize.getText());
		}
		catch (Exception e)
		{
			return 10;
		}
	}
	
	public boolean isControlSpaceSends()
	{
		return controlSpaceSends.isChecked();
	}

	public boolean isCurrentTaskInTitlebar()
	{
		return currentTaskInTitlebar.isChecked();
	}
	
	public String renderTitlebar(String currentTaskDescription)
	{
		return titleBarTemplate.getText().replaceAll(OptionsPanel.Token.CurrentTask, currentTaskDescription);
	}

	private void readPrefs()
	{
		username.setText(Cookies.getCookie(PrefKeys.User));
		maxSize.setText(Cookies.getCookie(PrefKeys.PageSize));
		controlSpaceSends.setChecked("true".equals(Cookies.getCookie(PrefKeys.CtrlSpaceSends)));
		currentTaskInTitlebar.setChecked("true".equals(Cookies.getCookie(PrefKeys.CurrentTaskInTitlebar)));
		titleBarTemplate.setText(Cookies.getCookie(PrefKeys.TitlebarTemplate));
	}
	
	private void initValues()
	{
		controller.getItemSvc().setBaseSvcUri(calculateServiceRoot());

		baseUri.setText(controller.getItemSvc().getBaseSvcUri());
		username.setText(controller.getItemSvc().getUsername());
		password.setText(controller.getItemSvc().getPassword());
		
		readPrefs();
		
		controller.getItemSvc().setUsername(username.getText());
		controller.getItemSvc().setPassword(password.getText());

		if (maxSize.getText().trim().isEmpty())
		{
			maxSize.setText("" + Defaults.MaxResults);
		}

		if (titleBarTemplate.getText().trim().isEmpty())
		{
			titleBarTemplate.setText(DefaultTitlebarTemplate);
		}
	}

	private void writePrefs()
	{
		Cookies.setCookie(PrefKeys.User, username.getText());
		Cookies.setCookie(PrefKeys.PageSize, maxSize.getText());
		Cookies.setCookie(PrefKeys.CtrlSpaceSends, (controlSpaceSends.isChecked() ? "true" : "false"));
		Cookies.setCookie(PrefKeys.CurrentTaskInTitlebar, (currentTaskInTitlebar.isChecked() ? "true" : "false"));
		Cookies.setCookie(PrefKeys.TitlebarTemplate, titleBarTemplate.getText());
	}
}
