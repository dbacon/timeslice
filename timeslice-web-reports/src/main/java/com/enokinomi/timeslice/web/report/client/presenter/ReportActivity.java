package com.enokinomi.timeslice.web.report.client.presenter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.core.TaskTotal;
import com.enokinomi.timeslice.web.core.client.ui.NavPanel;
import com.enokinomi.timeslice.web.core.client.ui.NotificationPanel;
import com.enokinomi.timeslice.web.core.client.ui.NotificationPanel.NotificationType;
import com.enokinomi.timeslice.web.core.client.util.RegistrationManager;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.prorata.client.core.Group;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.report.client.presenter.IReportPresenter.IReportsPresenterListener;
import com.enokinomi.timeslice.web.report.client.ui.IParamPanel;
import com.enokinomi.timeslice.web.report.client.ui.IParamPanel.IParamChangedListener;
import com.enokinomi.timeslice.web.report.client.ui.IReportPanel;
import com.enokinomi.timeslice.web.report.client.ui.IReportPanel.IReportPanelListener;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter.Listener;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ReportActivity extends AbstractActivity
{
    private final Provider<IReportPanel> widgetProvider;
    private final ReportPlace place;
    private final ISettingsPresenter settingsPresenter;
    private final IProrataManagerPresenter prorataPresenter;

    RegistrationManager registrations = new RegistrationManager();
    private final IReportPresenter presenter;

    @Inject
    ReportActivity(Provider<IReportPanel> widgetProvider, ReportPlace place, ILoginSupport loginSupport, IReportPresenter presenter, ISettingsPresenter settingsPresenter, IProrataManagerPresenter prorataPresenter)
    {
        this.widgetProvider = widgetProvider;
        this.place = place;
        this.presenter = presenter;
        this.settingsPresenter = settingsPresenter;
        this.prorataPresenter = prorataPresenter;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus)
    {
        final IReportPanel widget = widgetProvider.get();


        // set initial state

        widget.setFullDaySelected(place.getWhen(), false);
        widget.selectTab(place.getPath(), false);


        // bind handlers in 2-way communication

//        registrations.add(widget.getNavPanel().addListener(new NavPanelListenerImplementation(controller, loginSupport)));
//        registrations.add(controller.addControllerListener("report-panel/footer binding", new ControllerListenerAdapterExtension(widget.getNavPanel())));
//        registrations.add(loginSupport.addLoginListener(new LoginListenerImplementation(widget, widget)));


        registrations.add(widget.getNavPanel().addListener(new NavPanel.Listener()
        {
            @Override public void supportLinkRequested() {}
            @Override public void serverInfoRequested() {}
            @Override public void navigateLinkClicked(Place place) {}
            @Override public void logoutRequested()
            {
                widget.clear();
            }
        }));

        final NotificationPanel notif = widget.getNotificationPanel();

        registrations

            // TODO: unwind this bit and put it here ?
            .addAll(widget.bindProrataBits(prorataPresenter, settingsPresenter))

            .add(prorataPresenter.addListener(new IProrataManagerPresenter.Listener()
                {
                    @Override
                    public void tasksUpdated()
                    {
                    }

                    @Override
                    public void removeComplete(String group, String name)
                    {
                        notif.addInfoMsg(NotificationType.info, "Removed component '" + name + "' from '" + group + "'.");
                    }

                    @Override
                    public void allGroupInfoChanged(List<Group> result)
                    {
                    }

                    @Override
                    public void addFailed(String msg)
                    {
                        notif.addInfoMsg(NotificationType.warning, msg);
                    }

                    @Override
                    public void addComplete(String group, String name)
                    {
                        notif.addInfoMsg(NotificationType.info, "Added component '" + name + "' to '" + group + "'.");
                    }
                }))

            .add(settingsPresenter.addListener(new Listener()
                {
                    @Override
                    public void userSettingsDone(Map<String, List<String>> result)
                    {
                        widget.getParamsPanel().restoreFromSettings(result);
                        widget.getTreeTable().understandSettings(result);
                    }

                    @Override
                    public void userSessionDataDone(Map<String, String> result)
                    {
                    }

                    @Override
                    public void settingsChanged()
                    {
                    }
                }))

            .addAll(widget.bindSubListeners())

            .add(widget.getParamsPanel().addParamChangedListener(new IParamChangedListener()
                {
                    public void paramChanged(IParamPanel source)
                    {
                        widget.update();
                    }

                    @Override
                    public void historyRequested(Date date)
                    {
                        // TODO: find a better way to send to broadcast., not knowing about 'input' package.
        //                placeController.goTo(new InputPlace("navigation-from-report", false, date));
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
                }))

            .add(widget.addReportPanelListener(new IReportPanelListener()
                {
                    @Override
                    public void refreshRequested(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
                    {
                        presenter.startRefreshTotals(
                                Integer.MAX_VALUE,
                                SortDir.desc,
                                startingTimeText, endingTimeText,
                                allowWords, ignoreWords);

                        presenter.startRefreshTotalsAssigned(
                                Integer.MAX_VALUE,
                                SortDir.desc,
                                startingTimeText, endingTimeText,
                                allowWords, ignoreWords);
                    }

                    @Override
                    public void billeeUpdateRequested(String description, String newBillee)
                    {
                        presenter.startAssignBillee(description, newBillee);
                    }

                    @Override
                    public void itemHistoryRequested(Date when)
                    {
                        // TODO: find a better way to send to broadcast., not knowing about 'input' package.
        //                placeController.goTo(new InputPlace("navigation-from-report", false, when));
                    }
                }))

            .add(presenter.addListener(new IReportsPresenterListener()
                {
                    @Override
                    public void onRefreshTotalsDone(List<TaskTotal> result)
                    {
                        GWT.log("results?");
                        widget.setResults(result);
                    }

                    @Override
                    public void onRefreshTotalsAssignedDone(List<AssignedTaskTotal> result)
                    {
                        widget.setResultsAssigned(result);
                        prorataPresenter.setStuff(result);
                    }

                    @Override
                    public void onAllBilleesDone(List<String> result)
                    {
                        widget.setBillees(result);
                    }

                    @Override
                    public void onAssignBilleeDone()
                    {
                        widget.update();
                    }
                }));

        widget.initialize("report-activity-starting");

        settingsPresenter.refreshUserSettings();

        // display it.

        panel.setWidget(widget.asWidget());
    }

    @Override
    public void onStop()
    {
        registrations.terminateAll();
    }
}
