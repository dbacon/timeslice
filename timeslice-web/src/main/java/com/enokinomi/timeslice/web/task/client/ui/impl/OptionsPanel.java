package com.enokinomi.timeslice.web.task.client.ui.impl;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.session.client.ui.SessionSettingsControlPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsProvider;
import com.enokinomi.timeslice.web.task.client.ui_one.api.ITimesliceApp.Defaults;
import com.enokinomi.timeslice.web.task.client.ui_one.api.PrefHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class OptionsPanel extends Composite implements IOptionsPanel, IOptionsProvider
{
    public static class PrefKeys
    {
        public static final String PageSize = "timeslice.options.pagesize";
        public static final String PageSizeSeconds = "timeslice.options.pagesizeseconds";
        public static final String User = "timeslice.options.user";
        public static final String CtrlSpaceSends = "timeslice.options.controlspacesends";
        public static final String AutoRefresh = "timeslice.options.autorefresh";
        public static final String AutoRefreshMs = "timeslice.options.autorefreshms";
        public static final String CurrentTaskInTitlebar = "timeslice.options.currenttaskintitlebar";
        public static final String TitlebarTemplate= "timeslice.options.titlebartemplate";
    }

    public static final String DefaultTitlebarTemplate = "[TS] " + IOptionsProvider.CurrentTaskToken;

    private final OptionsConstants constants = GWT.create(OptionsConstants.class);

    private final TextBox maxSize = new TextBox();
    private final TextBox maxSeconds = new TextBox();
//    private final TextBox baseUri = new TextBox();
//
//    private final TextBox username = new TextBox();
//    private final PasswordTextBox password = new PasswordTextBox();

    private final CheckBox controlSpaceSends = new CheckBox(constants.controlSpaceAlsoSends());
    private final CheckBox currentTaskInTitlebar = new CheckBox(constants.showCurrentTaskInPageTitle());
    private final TextBox titleBarTemplate = new TextBox();
    private final CheckBox autoRefresh = new CheckBox(constants.autoRefresh());
    private final TextBox autoRefreshMs = new TextBox();

    private final List<IOptionsListener> listeners = new ArrayList<IOptionsListener>();

    public Widget asWidget() { return this; }

    @Override
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

    private Label createTitledLabel(String text, String title)
    {
        Label l1 = new Label(text, false);
        l1.setTitle(title);
        return l1;
    }

    @Inject
    public OptionsPanel(SessionSettingsControlPanel sscp)
    {
        localWidgetsInit();

        int row = 0;
        FlexTable optionsTable = new FlexTable();
//        optionsTable.setWidget(row,   0, createTitledLabel("Base URI", "Service root is here, should be autodected."));
//        optionsTable.setWidget(row++, 1, baseUri);
//        optionsTable.setWidget(row,   0, createTitledLabel("Username", "You put this in the ACL on the service side."));
//        optionsTable.setWidget(row++, 1, username);
//        optionsTable.setWidget(row,   0, createTitledLabel("Password", "Your password."));
//        optionsTable.setWidget(row++, 1, password);
        optionsTable.setWidget(row,   0, createTitledLabel(constants.maxResults(), constants.maxResultsHint()));
        optionsTable.setWidget(row++, 1, maxSize);
        optionsTable.setWidget(row,   0, createTitledLabel(constants.maxHours(), constants.maxHoursHint()));
        optionsTable.setWidget(row++, 1, maxSeconds);
        optionsTable.setWidget(row++, 0, controlSpaceSends);
        optionsTable.setWidget(row,   0, currentTaskInTitlebar);
        optionsTable.setWidget(row++, 1, titleBarTemplate);
        optionsTable.setWidget(row,   0, autoRefresh);
        optionsTable.setWidget(row++, 1, autoRefreshMs);

        addOptionsListener(new IOptionsListener()
        {
            public void optionsChanged(IOptionsPanel source)
            {
                writePrefs();
            }
        });

        DockLayoutPanel dp = new DockLayoutPanel(Unit.EM);
        dp.addNorth(optionsTable, 15);
        dp.add(new ScrollPanel(sscp));
        initWidget(dp);
    }

    private ClickHandler CommonClickFireChanged = new ClickHandler()
    {
        @Override
        public void onClick(ClickEvent event)
        {
            fireChanged();
        }
    };

    private ChangeHandler CommonChangeFireChanged = new ChangeHandler()
    {
        @Override
        public void onChange(ChangeEvent event)
        {
            fireChanged();
        }
    };

    private void localWidgetsInit()
    {
        maxSize.addChangeHandler(CommonChangeFireChanged);
        maxSeconds.addChangeHandler(CommonChangeFireChanged);

        controlSpaceSends.addClickHandler(CommonClickFireChanged);
        autoRefresh.addClickHandler(CommonClickFireChanged);
        autoRefreshMs.addChangeHandler(CommonChangeFireChanged);

        titleBarTemplate.addChangeHandler(CommonChangeFireChanged);

        currentTaskInTitlebar.addClickHandler(CommonClickFireChanged);
        currentTaskInTitlebar.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                consistentize();
            }
        });

        initValues();

        consistentize();
    }

    private void consistentize()
    {
        titleBarTemplate.setEnabled(currentTaskInTitlebar.getValue());
    }

    public int getMaxSize()
    {
        try
        {
            return Integer.valueOf(maxSize.getText());
        }
        catch (Exception e)
        {
            return Defaults.MaxResults;
        }
    }

    public long getMaxSeconds()
    {
        try
        {
            return Math.round(Double.parseDouble(maxSeconds.getText()) * 60 * 60);
        }
        catch (NumberFormatException e)
        {
            return Defaults.MaxSeconds;
        }
    }

    public boolean isControlSpaceSends()
    {
        return controlSpaceSends.getValue();
    }

    public boolean isAutoRefresh()
    {
        return autoRefresh.getValue();
    }

    public int getAutoRefreshMs()
    {
        try
        {
            return Integer.parseInt(autoRefreshMs.getText());
        }
        catch(NumberFormatException e)
        {
            return 500;
        }
    }

    public boolean isCurrentTaskInTitlebar()
    {
        return currentTaskInTitlebar.getValue();
    }

    @Override
    public String getTitleBarTemplate()
    {
        return titleBarTemplate.getText();
    }

    private void readPrefs()
    {
//        username.setText(Cookies.getCookie(PrefKeys.User));
        maxSize.setText(Cookies.getCookie(PrefKeys.PageSize));
        maxSeconds.setText(Cookies.getCookie(PrefKeys.PageSizeSeconds));
        controlSpaceSends.setValue("true".equals(Cookies.getCookie(PrefKeys.CtrlSpaceSends)));
        currentTaskInTitlebar.setValue("true".equals(Cookies.getCookie(PrefKeys.CurrentTaskInTitlebar)));
        titleBarTemplate.setText(Cookies.getCookie(PrefKeys.TitlebarTemplate));
        autoRefresh.setValue("true".equals(Cookies.getCookie(PrefKeys.AutoRefresh)));
        autoRefreshMs.setText(Cookies.getCookie(PrefKeys.AutoRefreshMs));
    }

    private void initValues()
    {
//        controller.getItemSvc().setBaseSvcUri(calculateServiceRoot());
//
//        baseUri.setText(controller.getItemSvc().getBaseSvcUri());
//        username.setText(controller.getItemSvc().getUsername());
//        password.setText(controller.getItemSvc().getPassword());

        readPrefs();

//        controller.getItemSvc().setUsername(username.getText());
//        controller.getItemSvc().setPassword(password.getText());

        if (maxSize.getText().trim().isEmpty())
        {
            maxSize.setText("" + Defaults.MaxResults);
        }

        if (maxSeconds.getText().trim().isEmpty())
        {
            maxSeconds.setText("" + Defaults.MaxSeconds / 60. / 60.);
        }

        if (titleBarTemplate.getText().trim().isEmpty())
        {
            titleBarTemplate.setText(DefaultTitlebarTemplate);
        }
        if (autoRefreshMs.getText().trim().isEmpty())
        {
            autoRefreshMs.setText("" + Defaults.AutoRefreshMs);
        }
    }

    private void writePrefs()
    {
//        Cookies.setCookie(PrefKeys.User, username.getText(), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.PageSize, maxSize.getText(), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.PageSizeSeconds, maxSeconds.getText(), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.CtrlSpaceSends, (controlSpaceSends.getValue() ? "true" : "false"), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.AutoRefresh, autoRefresh.getValue() ? "true" : "false", PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.AutoRefreshMs, autoRefreshMs.getText(), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.CurrentTaskInTitlebar, (currentTaskInTitlebar.getValue() ? "true" : "false"), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.TitlebarTemplate, titleBarTemplate.getText(), PrefHelper.createDateSufficientlyInTheFuture());
    }
}
