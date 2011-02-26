package com.enokinomi.timeslice.web.task.client.ui_one.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanel;
import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanelListener;
import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.ui.IClearable;
import com.enokinomi.timeslice.web.core.client.ui.Initializable;
import com.enokinomi.timeslice.web.core.client.ui.NavPanel;
import com.enokinomi.timeslice.web.core.client.ui.Registration;
import com.enokinomi.timeslice.web.core.client.ui.SimpleLayoutPanel;
import com.enokinomi.timeslice.web.core.client.ui.SortDir;
import com.enokinomi.timeslice.web.core.client.util.AsyncResult;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.LoginListener;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter.Listener;
import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamChangedListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IReportPanelListener;
import com.enokinomi.timeslice.web.task.client.ui_one.api.GenericActivityMapper.ActivityFactory;
import com.enokinomi.timeslice.web.task.client.ui_one.impl.ControllerListenerAdapter;
import com.enokinomi.timeslice.web.task.client.ui_one.impl.InputPanel;
import com.enokinomi.timeslice.web.task.client.ui_one.impl.InputPanel.InputListener;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.WithTokenizers;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class TopLevel implements EntryPoint
{

    @WithTokenizers({
        InputPlace.Tokenizer.class,
        ReportPlace.Tokenizer.class,
        OptionsPlace.Tokenizer.class,
        AppJobPlace.Tokenizer.class})
    public static interface TimeslicePlaceHistoryMapper extends PlaceHistoryMapper { }

    public static class InputActivityFactory implements ActivityFactory
    {
        private final Provider<InputPanel> widgetProvider;
        private final IController controller;
        private final ILoginSupport loginSupport;
        private final PlaceController placeController;
        private final ISettingsPresenter settingsPresenter;

        @Inject
        public InputActivityFactory(Provider<InputPanel> widgetProvider, PlaceController placeController, IController controller, ILoginSupport loginSupport, ISettingsPresenter settingsPresenter)
        {
            this.widgetProvider = widgetProvider;
            this.placeController = placeController;
            this.controller = controller;
            this.loginSupport = loginSupport;
            this.settingsPresenter = settingsPresenter;
        }

        public InputActivity get(Place place)
        {
            InputPlace inputPlace = (InputPlace) place;

            return new InputActivity("factory:from-place(" + inputPlace.creator + inputPlace.current + "," + inputPlace.when + ")",  widgetProvider, inputPlace, placeController, controller, loginSupport, settingsPresenter);
        }
    }

    public static class ReportActivityFactory implements ActivityFactory
    {
        private final Provider<IReportPanel> widgetProvider;
        private final IController controller;
        private final ILoginSupport loginSupport;
        private final ISettingsPresenter settingsPresenter;
        private final IProrataManagerPresenter prorataPresenter;
        private final PlaceController placeController;

        @Inject
        public ReportActivityFactory(Provider<IReportPanel> widgetProvider, PlaceController placeController, IController controller, ILoginSupport loginSupport, ISettingsPresenter settingsPresenter, IProrataManagerPresenter prorataPresenter)
        {
            this.widgetProvider = widgetProvider;
            this.placeController = placeController;
            this.controller = controller;
            this.loginSupport = loginSupport;
            this.settingsPresenter = settingsPresenter;
            this.prorataPresenter = prorataPresenter;
        }

        public ReportActivity get(Place place)
        {
            ReportPlace reportPlace = (ReportPlace) place;

            return new ReportActivity(widgetProvider, reportPlace, placeController, loginSupport, controller, settingsPresenter, prorataPresenter);
        }
    }

    public static class OptionsActivityFactory implements ActivityFactory
    {
        private final Provider<IOptionsPanel> widgetProvider;
        private final IController controller;
        private final ILoginSupport loginSupport;
        private final ISettingsPresenter settingsPresenter;

        @Inject
        public OptionsActivityFactory(Provider<IOptionsPanel> widgetProvider, IController controller, ILoginSupport loginSupport, ISettingsPresenter settingsPresenter)
        {
            this.widgetProvider = widgetProvider;
            this.controller = controller;
            this.loginSupport = loginSupport;
            this.settingsPresenter = settingsPresenter;
        }

        public OptionsActivity get(Place place)
        {
            OptionsPlace specificPlace = (OptionsPlace) place;

            return new OptionsActivity(widgetProvider, specificPlace, controller, loginSupport, settingsPresenter);
        }
    }

    public static class AppJobActivityFactory implements ActivityFactory
    {
        private final Provider<IAppJobPanel> widgetProvider;
        private final IController controller;
        private final ILoginSupport loginSupport;

        @Inject
        public AppJobActivityFactory(Provider<IAppJobPanel> widgetProvider, IController controller, ILoginSupport loginSupport)
        {
            this.widgetProvider = widgetProvider;
            this.controller = controller;
            this.loginSupport = loginSupport;
        }

        public AppJobActivity get(Place place)
        {
            AppJobPlace specificPlace = (AppJobPlace) place;

            return new AppJobActivity(widgetProvider, specificPlace, controller, loginSupport);
        }
    }

    public static class ActivityMapRegistration
    {
        private final GenericActivityMapper genericActivityMapper;
        private final InputActivityFactory inputActivityFactory;
        private final ReportActivityFactory reportActivityFactory;
        private final OptionsActivityFactory optionsActivityFactory;
        private final AppJobActivityFactory appJobActivityFactory;

        @Inject
        public ActivityMapRegistration(
                GenericActivityMapper genericActivityMapper,
                InputActivityFactory inputActivityFactory,
                ReportActivityFactory reportActivityFactory,
                OptionsActivityFactory optionsActivityFactory,
                AppJobActivityFactory appJobActivityFactory
                )
        {
            this.genericActivityMapper = genericActivityMapper;
            this.inputActivityFactory = inputActivityFactory;
            this.reportActivityFactory = reportActivityFactory;
            this.optionsActivityFactory = optionsActivityFactory;
            this.appJobActivityFactory = appJobActivityFactory;
        }

        public void mapAll()
        {
            genericActivityMapper.map(InputPlace.class, inputActivityFactory);
            genericActivityMapper.map(ReportPlace.class, reportActivityFactory);
            genericActivityMapper.map(OptionsPlace.class, optionsActivityFactory);
            genericActivityMapper.map(AppJobPlace.class, appJobActivityFactory);
        }
    }

    @Override
    public void onModuleLoad()
    {
        UiOneGinjector injector = GWT.create(UiOneGinjector.class);

        SimpleLayoutPanel container = new SimpleLayoutPanel();

        injector.getActivityManager().setDisplay(container);

        injector.getRegistration().mapAll();

        PlaceHistoryHandler placeHistoryHandler = injector.getPlaceHistoryHandler();
        placeHistoryHandler.register(
                injector.getPlaceController(),
                injector.getEventBus(),
                new InputPlace("default-place", true, null));

        RootLayoutPanel.get().add(container);

        placeHistoryHandler.handleCurrentHistory();
    }

    public static class InputPlace extends Place
    {
        private final boolean current;
        private final Date when;
        private final String creator;

        public static class Tokenizer implements PlaceTokenizer<InputPlace>
        {
            @Override
            public InputPlace getPlace(String token)
            {
                GWT.log("input.get-place: '" + token + "'");
                String[] pieces = token.split("/");

                if (pieces.length > 0)
                {
                    String one = pieces[0];

                    if ("current".equals(one))
                    {
                        // same as default.
                    }
                    else if ("history".equals(one))
                    {
                        if (pieces.length > 1)
                        {
                            String date = pieces[1];
                            Date when = DateTimeFormat.getFormat("yyyy-MM-dd").parse(date);
                            return new InputPlace("from-token:" + token, false, when);
                        }
                        else
                        {
                            return new InputPlace("from-token:" + token, false, null);
                        }
                    }
                    else
                    {
                        // leave as default
                    }
                }

                return new InputPlace("from-token:" + token, true, null);
            }

            @Override
            public String getToken(InputPlace place)
            {
                if (place.current) return "current";
                String token = "history" + "/" + DateTimeFormat.getFormat("yyyy-MM-dd").format(place.when);
                GWT.log("Got token for input-place: " + token);
                return token;
            }
        }

        public InputPlace(String creator, boolean current, Date when)
        {
            this.creator = creator;
            this.current = current;
            this.when = when;
        }

        @Override
        public String toString()
        {
            return "Input";
        }

    }

    public static class FooterListenerImplementation implements NavPanel.Listener
    {
        private IController controller;
        private ILoginSupport loginSupport;

        public FooterListenerImplementation(IController controller, ILoginSupport loginSupport)
        {
            this.controller = controller;
            this.loginSupport = loginSupport;
        }

        @Override
        public void logoutRequested()
        {
            loginSupport.logout();
        }

        @Override
        public void serverInfoRequested()
        {
            controller.serverInfo();
        }

        @Override
        public void supportLinkRequested()
        {
            // TODO: do something?
            GWT.log("support-link clicked");
        }

        @Override
        public void navigateLinkClicked(Place place)
        {
            // taken care of by provider in gwt module.
        }
    }

    public static class ControllerListenerAdapterExtension extends ControllerListenerAdapter
    {
        private final NavPanel navPanel;

        private ControllerListenerAdapterExtension(NavPanel navPanel)
        {
            this.navPanel = navPanel;
        }

        @Override
        public void serverInfoRecieved(String info)
        {
            navPanel.setServerInfo(info);
        }
    }

    public static class LoginListenerImplementation implements LoginListener
    {
        private final IClearable clearable;
        private final Initializable initializable;

        public LoginListenerImplementation(IClearable clearable, Initializable initializable)
        {
            this.clearable = clearable;
            this.initializable = initializable;
        }

        @Override
        public void sessionEnded(boolean retry)
        {
            clearable.clear();
        }

        @Override
        public void newSessionStarted()
        {
            initializable.initialize("login-listener.new-session");
        }
    }

    public static class InputActivity extends AbstractActivity
    {
        private final Provider<InputPanel> widgetProvider;
        private final InputPlace place;
        private final IController controller;
        private final ILoginSupport loginSupport;
        @SuppressWarnings("unused") private final PlaceController placeController;
        private final String creator;
        private final ISettingsPresenter settingsPresenter;

        private final RegistrationManager registrations = new RegistrationManager();

        @Inject
        InputActivity(String creator, Provider<InputPanel> widgetProvider, InputPlace place,  PlaceController placeController, IController controller, ILoginSupport loginSupport, ISettingsPresenter settingsPresenter)
        {
            this.creator = creator;
            this.widgetProvider = widgetProvider;
            this.place = place;
            this.placeController = placeController;
            this.controller = controller;
            this.loginSupport = loginSupport;
            this.settingsPresenter = settingsPresenter;
        }

        @Override
        public void start(AcceptsOneWidget panel, EventBus eventBus)
        {
            GWT.log("starting input-activity - creator: " + creator);
            final InputPanel widget = widgetProvider.get();

            // set initial state
            widget.setHistoryMode(!place.current, false);
            widget.setHistoricDate(place.when, false);

            // bind handlers

            registrations.add(widget.getNavPanel().addListener(new FooterListenerImplementation(controller, loginSupport)));
            registrations.add(controller.addControllerListener("input-panel/footer binding", new ControllerListenerAdapterExtension(widget.getNavPanel())));
            registrations.add(loginSupport.addLoginListener(new LoginListenerImplementation(widget, widget)));

            registrations.add(widget.addListener(new InputListener()
            {
                @Override
                public void editTagRequested(StartTag editedStartTag)
                {
                    controller.startEditDescription(editedStartTag);
                }

                @Override
                public void addTagRequested(String instantString, String description)
                {
                    controller.startAddItem(instantString, description);
                }

                @Override
                public void refreshRequested(int maxItems, String starting, String ending)
                {
                    GWT.log("refresh-requested: " + maxItems + ", " + starting + " ~ " + ending);
                    controller.startRefreshItems(maxItems, starting, ending);
                }
            }));

            registrations.add(controller.addControllerListener("input-panel/items", new ControllerListenerAdapter()
            {
                @Override
                public void onRefreshItemsDone(AsyncResult<List<StartTag>> result)
                {
                    if (result.isError())
                    {
                        GWT.log(result.getThrown().toString() + " (TODO: fix error propagation and reporting)");
                    }
                    else
                    {
                        widget.itemsRefreshed(result.getReturned());
                    }
                }

                @Override
                public void onAddItemDone(AsyncResult<Void> result)
                {
                    widget.itemAdded();
                }
            }));

            registrations.add(settingsPresenter.addListener(new ISettingsPresenter.Listener()
            {
                @Override public void userSessionDataDone(Map<String, String> result) { }
                @Override public void settingsChanged() { }

                @Override
                public void userSettingsDone(Map<String, List<String>> result)
                {
                    widget.handleUserSettings(result);
                    widget.initialize("settings-done");
                }

            }));

            settingsPresenter.refreshRequested();

            // display it.
            panel.setWidget(widget.asWidget());
        }

        @Override
        public void onStop()
        {
            registrations.terminateAll();
        }
    }

    public static class ReportPlace extends Place
    {
        private final Date when;
        private final String path;

        public static class Tokenizer implements PlaceTokenizer<ReportPlace>
        {
            @Override
            public ReportPlace getPlace(String token)
            {
                String path = null;
                Date when = null;

                if (token != null)
                {
                    String[] pieces = token.split(";");

                    for (String piece: pieces)
                    {
                        String[] nv = piece.split("=");
                        if (nv.length == 2)
                        {
                            String name = nv[0];
                            String value = nv[1];

                            if ("d".equals(name))
                            {
                                try
                                {
                                    when = DateTimeFormat.getFormat("yyyy-MM-dd").parse(value);
                                }
                                catch (Exception e)
                                {
                                }
                            }
                            else if ("p".equals(name))
                            {
                                path = value;
                            }
                        }
                    }
                }

                return new ReportPlace(when, path);
            }

            @Override
            public String getToken(ReportPlace place)
            {
                String t = "";
                if (place.when != null)
                {
                    if (t.length() > 0) t += ";";
                    t += "d=" + DateTimeFormat.getFormat("yyyy-MM-dd").format(place.when);
                }
                if (place.path != null)
                {
                    if (t.length() > 0) t += ";";
                    t += "p=" + place.path;
                }

                return t;
            }
        }

        public ReportPlace(Date when, String path)
        {
            this.when = when;
            this.path = path;
        }

        @Override
        public String toString()
        {
            return "Reports";
        }

    }

    public static class RegistrationManager
    {
        private List<Registration> registrations = new ArrayList<Registration>();

        public void add(Registration r)
        {
            registrations.add(r);
        }

        public void terminateAll()
        {
            for (Registration r: registrations) r.terminate();
        }
    }

    public static class ReportActivity extends AbstractActivity
    {
        private final Provider<IReportPanel> widgetProvider;
        private final ReportPlace place;
        private final ILoginSupport loginSupport;
        private final IController controller;
        private final ISettingsPresenter settingsPresenter;
        private final IProrataManagerPresenter prorataPresenter;
        private final PlaceController placeController;

        RegistrationManager registrations = new RegistrationManager();

        @Inject
        ReportActivity(Provider<IReportPanel> widgetProvider, ReportPlace place, PlaceController placeController, ILoginSupport loginSupport, IController controller, ISettingsPresenter settingsPresenter, IProrataManagerPresenter prorataPresenter)
        {
            this.widgetProvider = widgetProvider;
            this.place = place;
            this.placeController = placeController;
            this.loginSupport = loginSupport;
            this.controller = controller;
            this.settingsPresenter = settingsPresenter;
            this.prorataPresenter = prorataPresenter;
        }

        @Override
        public void start(AcceptsOneWidget panel, EventBus eventBus)
        {
            final IReportPanel widget = widgetProvider.get();


            // set initial state

            widget.setFullDaySelected(place.when, false);
            widget.selectTab(place.path, false);


            // bind handlers in 2-way communication

            registrations.add(widget.getNavPanel().addListener(new FooterListenerImplementation(controller, loginSupport)));
            registrations.add(controller.addControllerListener("report-panel/footer binding", new ControllerListenerAdapterExtension(widget.getNavPanel())));
            registrations.add(loginSupport.addLoginListener(new LoginListenerImplementation(widget, widget)));

            // TODO: unwind this bit and put it here.
            widget.bindProrataBits(prorataPresenter, settingsPresenter);

            registrations.add(settingsPresenter.addListener(new Listener()
            {
                @Override
                public void userSettingsDone(Map<String, List<String>> result)
                {
                    widget.getParamsPanel().restoreFromSettings(result);
                }

                @Override
                public void userSessionDataDone(Map<String, String> result)
                {
                }

                @Override
                public void settingsChanged()
                {
                }
            }));

            registrations.add(widget.getParamsPanel().addParamChangedListener(new IParamChangedListener()
            {
                public void paramChanged(IParamPanel source)
                {
                    widget.update();
                }

                @Override
                public void historyRequested(Date date)
                {
                    placeController.goTo(new InputPlace("navigation-from-report", false, date));
                }

                @Override
                public void allowWordsChanged(String allowWords)
                {
                    settingsPresenter.userSettingCreateOrUpdateRequested("ui.params.allowwords", allowWords);
                }

                @Override
                public void ignoreWordsChanged(String ignoreWords)
                {
                    settingsPresenter.userSettingCreateOrUpdateRequested("ui.params.ignorewords", ignoreWords);
                }
            }));

            registrations.add(widget.addReportPanelListener(new IReportPanelListener()
            {
                @Override
                public void refreshRequested(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
                {
                    controller.startRefreshTotals(
                            Integer.MAX_VALUE,
                            SortDir.desc,
                            startingTimeText, endingTimeText,
                            allowWords, ignoreWords);

                    controller.startRefreshTotalsAssigned(
                            Integer.MAX_VALUE,
                            SortDir.desc,
                            startingTimeText, endingTimeText,
                            allowWords, ignoreWords);
                }

                @Override
                public void billeeUpdateRequested(String description, String newBillee)
                {
                    controller.startAssignBillee(description, newBillee);
                }

                @Override
                public void itemHistoryRequested(Date when)
                {
                    placeController.goTo(new InputPlace("navigation-from-report", false, when));
                }
            }));

            registrations.add(controller.addControllerListener("report-panel/functions binding", new ControllerListenerAdapter()
            {
                @Override
                public void onRefreshTotalsDone(AsyncResult<List<TaskTotal>> result)
                {
                    if (result.isError())
                    {
                        GWT.log("report-panel refresh-totals-done error: " + result.getThrown().getMessage(), result.getThrown());
                    }
                    else
                    {
                        GWT.log("report-panel refresh-totals-done: " + result.getReturned().size());
                        widget.setResults(result.getReturned());
                    }
                }

                @Override
                public void onRefreshTotalsAssignedDone(AsyncResult<List<AssignedTaskTotal>> result)
                {
                    if (result.isError())
                    {
                        GWT.log("report-panel refresh-totals-assigned-done error: " + result.getThrown().getMessage(), result.getThrown());
                    }
                    else
                    {
                        GWT.log("report-panel refresh-totals-assigned-done: " + result.getReturned().size());
                        widget.setResultsAssigned(result.getReturned());
                        prorataPresenter.setStuff(result.getReturned());
                    }
                }

                @Override
                public void onAllBilleesDone(AsyncResult<List<String>> result)
                {
                    if (result.isError())
                    {
                        GWT.log("all billees failed: " + result.getThrown().toString());
                    }
                    else
                    {
                        GWT.log("got billees list, setting on UI");
                        widget.setBillees(result.getReturned());
                    }
                }

                @Override
                public void onAssignBilleeDone(AsyncResult<Void> result)
                {
                    if (result.isError())
                    {
                    }
                    else
                    {
                        widget.update();
                    }
                }
            }));

            widget.initialize("report-activity-starting");

            settingsPresenter.refreshRequested();
            controller.startGetAllBillees();

            // display it.

            panel.setWidget(widget.asWidget());
        }

        @Override
        public void onStop()
        {
            registrations.terminateAll();
        }
    }

    public static class OptionsPlace extends Place
    {
        public static class Tokenizer implements PlaceTokenizer<OptionsPlace>
        {
            @Override
            public OptionsPlace getPlace(String token)
            {
                if (token != null)
                {
                }

                return new OptionsPlace();
            }

            @Override
            public String getToken(OptionsPlace place)
            {
                return "";
            }
        }

        public OptionsPlace()
        {
        }

        @Override
        public String toString()
        {
            return "Options";
        }

    }

    public static class OptionsActivity extends AbstractActivity
    {
        private final Provider<IOptionsPanel> widgetProvider;

        @SuppressWarnings("unused")
        private final OptionsPlace place;
        private final IController controller;
        private final ILoginSupport loginSupport;
        private final ISettingsPresenter settingsPresenter;

        private RegistrationManager registrations = new RegistrationManager();

        @Inject
        OptionsActivity(Provider<IOptionsPanel> widgetProvider, OptionsPlace place, IController controller, ILoginSupport loginSupport, ISettingsPresenter settingsPresenter)
        {
            this.widgetProvider = widgetProvider;
            this.place = place;
            this.controller = controller;
            this.loginSupport = loginSupport;
            this.settingsPresenter = settingsPresenter;
        }

        @Override
        public void start(AcceptsOneWidget panel, EventBus eventBus)
        {
            final IOptionsPanel widget = widgetProvider.get();

            registrations.add(widget.getNavPanel().addListener(new FooterListenerImplementation(controller, loginSupport)));
            registrations.add(controller.addControllerListener("options-panel/footer binding", new ControllerListenerAdapterExtension(widget.getNavPanel())));
            registrations.add(loginSupport.addLoginListener(new LoginListenerImplementation(widget, widget)));

            registrations.add(widget.addListener(new IOptionsPanel.Listener()
            {
                @Override
                public void userSettingEditRequested(String name, String oldValue, String newValue)
                {
                    settingsPresenter.userSettingEditRequested(name, oldValue, newValue);
                }

                @Override
                public void userSettingDeleteRequested(String name, String value)
                {
                    settingsPresenter.userSettingDeleteRequested(name, value);
                }

                @Override
                public void userSettingCreateOrUpdate(String name, String value)
                {
                    settingsPresenter.userSettingCreateOrUpdateRequested(name, value);
                }

                @Override
                public void userSettingAddRequested(String name, String value)
                {
                    settingsPresenter.userSettingAddRequested(name, value);
                }

                @Override
                public void refreshRequested()
                {
                    settingsPresenter.refreshRequested();
                }
            }));

            registrations.add(settingsPresenter.addListener(new ISettingsPresenter.Listener()
            {
                @Override
                public void userSettingsDone(Map<String, List<String>> result)
                {
                    widget.setUserSettings(result);
                }

                @Override
                public void userSessionDataDone(Map<String, String> result)
                {
                    widget.setSessionData(result);
                }

                @Override
                public void settingsChanged()
                {
                    widget.update();
                }
            }));

//            widget.initialize();

            panel.setWidget(widget.asWidget());
        }

        @Override
        public void onStop()
        {
            registrations.terminateAll();
        }
    }

    public static class AppJobPlace extends Place
    {
        public static class Tokenizer implements PlaceTokenizer<AppJobPlace>
        {
            @Override
            public AppJobPlace getPlace(String token)
            {
                return new AppJobPlace();
            }

            @Override
            public String getToken(AppJobPlace place)
            {
                return "";
            }
        }

        public AppJobPlace()
        {
        }

        @Override
        public String toString()
        {
            return "Maintenance";
        }

    }

    public static class AppJobActivity extends AbstractActivity
    {
        private final Provider<IAppJobPanel> widgetProvider;

        @SuppressWarnings("unused")
        private final AppJobPlace place;

        private final IController controller;
        private final ILoginSupport loginSupport;

        private final RegistrationManager registrations = new RegistrationManager();

        @Inject
        AppJobActivity(Provider<IAppJobPanel> widgetProvider, AppJobPlace place, IController controller, ILoginSupport loginSupport)
        {
            this.widgetProvider = widgetProvider;
            this.place = place;
            this.controller = controller;
            this.loginSupport = loginSupport;
        }

        @Override
        public void start(AcceptsOneWidget panel, EventBus eventBus)
        {
            final IAppJobPanel widget = widgetProvider.get();

            registrations.add(widget.getNavPanel().addListener(new FooterListenerImplementation(controller, loginSupport)));
            registrations.add(controller.addControllerListener("appjob-panel/footer binding", new ControllerListenerAdapterExtension(widget.getNavPanel())));
            registrations.add(loginSupport.addLoginListener(new LoginListenerImplementation(widget, widget)));

            registrations.add(widget.addListener(new IAppJobPanelListener()
            {
                @Override
                public void appJobRequested(String jobId)
                {
                    controller.startPerformJob(jobId);
                }

                @Override
                public void appJobListRefreshRequested()
                {
                    controller.startListAvailableJobs();
                }
            }));

            registrations.add(controller.addControllerListener("appjob-panel/function", new ControllerListenerAdapter()
            {
                @Override
                public void onListAvailableJobsDone(AsyncResult<List<String>> result)
                {
                    if (result.isError())
                    {
                    }
                    else
                    {
                        widget.redisplayJobIds(result.getReturned());
                    }
                }

                @Override
                public void onPerformJobDone(AsyncResult<AppJobCompletion> result)
                {
                    if (result.isError())
                    {
                    }
                    else
                    {
                        AppJobCompletion completion = result.getReturned();
                        widget.addResult(completion.getJobId(), completion.getStatus(), completion.getDescription());
                    }
                }
            }));

            widget.initialize("app-job.activity-starting");

            panel.setWidget(widget.asWidget());
        }

        @Override
        public void onStop()
        {
            registrations.terminateAll();
        }
    }

}
