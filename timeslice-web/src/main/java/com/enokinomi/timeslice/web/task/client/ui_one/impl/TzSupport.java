package com.enokinomi.timeslice.web.task.client.ui_one.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.LoginListener;
import com.enokinomi.timeslice.web.settings.client.core.ISettingsSvcAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class TzSupport
{
    private final ILoginSupport loginSupport;
    private final ISettingsSvcAsync settingSvc;
    private int tzOffsetMin = 0;

    @Inject
    public TzSupport(final ILoginSupport loginSupport, final ISettingsSvcAsync settingSvc)
    {
        this.loginSupport = loginSupport;
        this.settingSvc = settingSvc;

        loginSupport.addLoginListener(new LoginListener()
        {
            @Override
            public void sessionEnded(boolean retry)
            {
            }

            @Override
            public void newSessionStarted()
            {
                loadUserTz();
            }
        });

        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                loadUserTz();
            }
        });
    }

    @SuppressWarnings("deprecation")
    public static int getTimezoneOffsetMinutesFromBrowser()
    {
        return new Date().getTimezoneOffset() * -1; // browsers report east of GMT as negative.
    }

    private void loadUserTz()
    {
        new ILoginSupport.IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                settingSvc.getSettings(loginSupport.getAuthToken(),
                        "",
                        loginSupport.withRetry(this, new AsyncCallback<Map<String, List<String>>>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                GWT.log("Could not get timezone setting.");
                            }

                            @Override
                            public void onSuccess(Map<String, List<String>> result)
                            {
                                List<String> values = result.get("usersession.tzoffsetmin");
                                if (values != null && !values.isEmpty())
                                {
                                    try
                                    {
                                        tzOffsetMin = Integer.valueOf(values.get(0));
                                    }
                                    catch (Exception e)
                                    {
                                        GWT.log("Could not parse usersettings.tzoffsetmin value: " + values.get(0));
                                    }
                                }
                                else
                                {
                                    setFromBrowser();
                                }
                            }
                        }));
            }
        }.runAsync();
    }

    public void setFromBrowser()
    {
        final int number = TzSupport.getTimezoneOffsetMinutesFromBrowser();

        new ILoginSupport.IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                settingSvc.addSetting(loginSupport.getAuthToken(),
                        "usersession.tzoffsetmin", "" + number,
                        loginSupport.withRetry(this, new AsyncCallback<Void>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                GWT.log("Failed auto-setting timezone: " + caught.getMessage());
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                loadUserTz(); // careful, could ping-pong if mis-information
                            }
                        }));
            }
        }.runAsync();
    }

    public String renderForClientMachine(Date date, int timezoneOffsetMinutes)
    {
//        test();
        timezoneOffsetMinutes *= -1; // seems to be a bug? see test(), could just be differing semantics, work with it for now.

        return DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ").format(date, TimeZone.createTimeZone(timezoneOffsetMinutes));
    }

//    // seems to be a bug?  Anyway, work with it for now. could just be semantic difference gwt vs. joda
//    private void test()
//    {
//        System.out.println("testing tz:");
//        for (int i = -13; i < 13; ++i)
//        {
//            System.out.println("  offset hrs " + i + ": " + DateTimeFormat.getFormat("ZZZ").format(new Date(), TimeZone.createTimeZone(i*60)));
//        }
//    }

    public String renderForClientMachine(Date date)
    {
        return renderForClientMachine(date, tzOffsetMin);
    }
}
