package com.enokinomi.timeslice.web.prorata.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.google.gwt.user.client.ui.IsWidget;

public interface IProjectListPanel extends IsWidget
{

    void clear();

    List<Registration> bind(IProrataManagerPresenter prorataManagerPresenter, ISettingsPresenter settingsPresenter);

}
