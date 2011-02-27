package com.enokinomi.timeslice.web.report.client.presenter;

import com.enokinomi.timeslice.web.core.client.util.IActivityFactory;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.report.client.ui.IReportPanel;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ReportActivityFactory implements IActivityFactory
{
    private final Provider<IReportPanel> widgetProvider;
    private final IReportPresenter presenter;
    private final ILoginSupport loginSupport;
    private final ISettingsPresenter settingsPresenter;
    private final IProrataManagerPresenter prorataPresenter;

    @Inject
    public ReportActivityFactory(Provider<IReportPanel> widgetProvider, IReportPresenter presenter, ILoginSupport loginSupport, ISettingsPresenter settingsPresenter, IProrataManagerPresenter prorataPresenter)
    {
        this.widgetProvider = widgetProvider;
        this.presenter = presenter;
        this.loginSupport = loginSupport;
        this.settingsPresenter = settingsPresenter;
        this.prorataPresenter = prorataPresenter;
    }

    public ReportActivity get(Place place)
    {
        ReportPlace reportPlace = (ReportPlace) place;

        return new ReportActivity(widgetProvider, reportPlace, loginSupport, presenter, settingsPresenter, prorataPresenter);
    }
}
