package com.enokinomi.timeslice.web.settings.client.presenter.impl;

import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.core.client.util.RegistrationManager;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.enokinomi.timeslice.web.settings.client.ui.api.IOptionsPanel;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class OptionsActivity extends AbstractActivity
    {
        private final Provider<IOptionsPanel> widgetProvider;

        @SuppressWarnings("unused")
        private final OptionsPlace place;
//        private final ILoginSupport loginSupport;
        private final ISettingsPresenter settingsPresenter;

        private RegistrationManager registrations = new RegistrationManager();

        @Inject
        OptionsActivity(Provider<IOptionsPanel> widgetProvider, OptionsPlace place, /*ILoginSupport loginSupport, */ISettingsPresenter settingsPresenter)
        {
            this.widgetProvider = widgetProvider;
            this.place = place;
//            this.loginSupport = loginSupport;
            this.settingsPresenter = settingsPresenter;
        }

        @Override
        public void start(AcceptsOneWidget panel, EventBus eventBus)
        {
            final IOptionsPanel widget = widgetProvider.get();

//            registrations.add(widget.getNavPanel().addListener(new NavPanelListenerImplementation(controller, loginSupport)));
//            registrations.add(controller.addControllerListener("options-panel/footer binding", new ControllerListenerAdapterExtension(widget.getNavPanel())));
//            registrations.add(loginSupport.addLoginListener(new LoginListenerImplementation(widget, widget)));

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
