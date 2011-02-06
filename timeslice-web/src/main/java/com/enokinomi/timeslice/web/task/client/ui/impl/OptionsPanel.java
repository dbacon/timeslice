package com.enokinomi.timeslice.web.task.client.ui.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.enokinomi.timeslice.web.core.client.ui.PrefHelper;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.IOnAuthenticated;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.LoginListener;
import com.enokinomi.timeslice.web.login.client.ui.impl.LoginSupport;
import com.enokinomi.timeslice.web.session.client.core.ISessionSvcAsync;
import com.enokinomi.timeslice.web.settings.client.core.ISettingsSvcAsync;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsProvider;
import com.enokinomi.timeslice.web.task.client.ui.impl.ISettingsEditorPanel.Listener;
import com.enokinomi.timeslice.web.task.client.ui_one.api.ITimesliceApp.Defaults;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
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
        public static final String CurrentTaskInTitlebar = "timeslice.options.currenttaskintitlebar";
        public static final String TitlebarTemplate= "timeslice.options.titlebartemplate";
    }

    public static final String DefaultTitlebarTemplate = "[TS] " + IOptionsProvider.CurrentTaskToken;

    private final OptionsConstants constants = GWT.create(OptionsConstants.class);
    private final ISettingsSvcAsync settingsSvc;
    private final ISessionSvcAsync sessionSvc;

    private final TextBox maxSize = new TextBox();
    private final TextBox maxSeconds = new TextBox();
//    private final TextBox baseUri = new TextBox();
//
//    private final TextBox username = new TextBox();
//    private final PasswordTextBox password = new PasswordTextBox();

    private final CheckBox controlSpaceSends = new CheckBox(constants.controlSpaceAlsoSends());
    private final CheckBox currentTaskInTitlebar = new CheckBox(constants.showCurrentTaskInPageTitle());
    private final TextBox titleBarTemplate = new TextBox();
    private final ISettingsEditorPanel settingsEditor;
    private final FlexTable sessionDataTable = new FlexTable();

    private final List<IOptionsListener> listeners = new ArrayList<IOptionsListener>();

    private final ILoginSupport loginSupport;

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
    OptionsPanel(final LoginSupport loginSupport, final ISettingsSvcAsync settingsSvc, final ISettingsEditorPanel settingsEditor, final ISessionSvcAsync sessionSvc)
    {
        this.loginSupport = loginSupport;
        this.settingsSvc = settingsSvc;
        this.settingsEditor = settingsEditor;
        this.sessionSvc = sessionSvc;

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

        addOptionsListener(new IOptionsListener()
        {
            public void optionsChanged(IOptionsPanel source)
            {
                writePrefs();
            }
        });

        loginSupport.addLoginListener(new LoginListener()
        {
            @Override
            public void sessionEnded(boolean retry)
            {
                sessionDataTable.removeAllRows();
                sessionDataTable.setText(0, 0, "Not logged in.");

                settingsEditor.setSettings(new LinkedHashMap<String, List<String>>());
            }

            @Override
            public void newSessionStarted()
            {
                updateSessionData();
                refresh();
            }
        });

        settingsEditor.addListener(new Listener()
        {
            @Override
            public void onRefreshButtonClicked()
            {
                refresh();
            }

            @Override
            public void onItemDeleted(String name, String value)
            {
                settingsSvc.deleteSetting(loginSupport.getAuthToken(), name, value, new AsyncCallback<Void>()
                {
                    @Override
                    public void onSuccess(Void result)
                    {
                        refresh();
                        updateSessionData();
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        GWT.log("Deleting item failed: " + caught.getMessage());
                    }
                });
            }

            @Override
            public void onItemEdited(String name, String oldValue, String newValue)
            {
                settingsSvc.editSetting(loginSupport.getAuthToken(), name, oldValue, newValue, new AsyncCallback<Void>()
                        {
                            @Override
                            public void onSuccess(Void result)
                            {
                                refresh();
                                updateSessionData();
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                GWT.log("Editing item failed: " + caught.getMessage());
                            }
                        });
            }

            @Override
            public void onItemAdded(String name, String value)
            {
                settingsSvc.addSetting(loginSupport.getAuthToken(), name, value, new AsyncCallback<Void>()
                {
                    @Override
                    public void onSuccess(Void result)
                    {
                        refresh();
                        updateSessionData();
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        GWT.log("Adding item failed: " + caught.getMessage());
                    }
                });
            }
        });

        DecoratorPanel pw = new DecoratorPanel();
        pw.add(sessionDataTable);

        VerticalPanel p = new VerticalPanel();
        p.setSpacing(5);
        p.add(pw);
        p.add(settingsEditor.asWidget());


        DockLayoutPanel dp = new DockLayoutPanel(Unit.EM);
        dp.addNorth(optionsTable, 15);
        dp.add(p);
        initWidget(dp);

        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                refresh();
                updateSessionData();
            }
        });
    }

    public void updateSessionData()
    {
        new LoginSupport.IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                sessionSvc.getSessionData(loginSupport.getAuthToken(),
                        loginSupport.withRetry(this, new AsyncCallback<Map<String,String>>()
                        {
                            @Override
                            public void onSuccess(Map<String, String> result)
                            {
                                sessionDataTable.clear();

                                int row = 0;
                                for (Entry<String, String> entry: result.entrySet())
                                {
                                    sessionDataTable.setText(row, 0, entry.getKey());
                                    sessionDataTable.setText(row, 1, entry.getValue());

                                    ++row;
                                }
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: log error
                            }
                        }));
            }
        }.runAsync();
    }

    private void refresh()
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                settingsSvc.getSettings(
                        loginSupport.getAuthToken(),
                        "",
                        loginSupport.withRetry(this, new AsyncCallback<Map<String,List<String>>>()
                        {
                            @Override
                            public void onSuccess(Map<String, List<String>> result)
                            {
                                settingsEditor.setSettings(result);
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                GWT.log("Getting session settings failed: " + caught.getMessage(), caught);
                            }
                        }));
            }
        }.runAsync();
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
    }

    private void writePrefs()
    {
//        Cookies.setCookie(PrefKeys.User, username.getText(), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.PageSize, maxSize.getText(), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.PageSizeSeconds, maxSeconds.getText(), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.CtrlSpaceSends, (controlSpaceSends.getValue() ? "true" : "false"), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.CurrentTaskInTitlebar, (currentTaskInTitlebar.getValue() ? "true" : "false"), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.TitlebarTemplate, titleBarTemplate.getText(), PrefHelper.createDateSufficientlyInTheFuture());
    }
}
