package com.enokinomi.timeslice.web.task.client.presenter;

import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.core.client.ui.NavPanel;
import com.enokinomi.timeslice.web.core.client.util.RegistrationManager;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.presenter.ITaskPresenter.ITaskPresenterListener;
import com.enokinomi.timeslice.web.task.client.ui.InputPanel;
import com.enokinomi.timeslice.web.task.client.ui.InputPanel.InputListener;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class InputActivity extends AbstractActivity
{
    private final Provider<InputPanel> widgetProvider;
    private final InputPlace place;
    private final ITaskPresenter presenter;
//    private final ILoginSupport loginSupport;
    @SuppressWarnings("unused") private final PlaceController placeController;
    private final String creator;
    private final ISettingsPresenter settingsPresenter;

    private final RegistrationManager registrations = new RegistrationManager();

    @Inject
    InputActivity(String creator, Provider<InputPanel> widgetProvider, InputPlace place,  PlaceController placeController, ITaskPresenter presenter, ISettingsPresenter settingsPresenter)
    {
        this.creator = creator;
        this.widgetProvider = widgetProvider;
        this.place = place;
        this.placeController = placeController;
        this.presenter = presenter;
        this.settingsPresenter = settingsPresenter;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus)
    {
        GWT.log("starting input-activity - creator: " + creator);
        final InputPanel widget = widgetProvider.get();

        // set initial state
        widget.setHistoryMode(!place.isCurrent(), true);
        widget.setHistoricDate(place.getWhen(), true);

        // bind handlers

//        registrations.add(widget.getNavPanel().addListener(new NavPanelListenerImplementation(controller, loginSupport)));
//        registrations.add(controller.addControllerListener("input-panel/footer binding", new ControllerListenerAdapterExtension(widget.getNavPanel())));
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

        registrations.add(widget.addListener(new InputListener()
        {
            @Override
            public void editTagRequested(StartTag editedStartTag)
            {
                presenter.startEditDescription(editedStartTag);
            }

            @Override
            public void addTagRequested(String instantString, String description)
            {
                presenter.startAddItem(instantString, description);
            }

            @Override
            public void refreshRequested(int maxItems, String starting, String ending)
            {
                presenter.startRefreshItems(maxItems, starting, ending);
            }
        }));

        registrations.add(presenter.addListener(new ITaskPresenterListener()
        {
            @Override
            public void onRefreshItemsDone(List<StartTag> result)
            {
                widget.itemsRefreshed(result);
            }

            @Override
            public void onAddItemDone()
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
