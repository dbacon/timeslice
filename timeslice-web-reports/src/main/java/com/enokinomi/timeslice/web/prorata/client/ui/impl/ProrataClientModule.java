package com.enokinomi.timeslice.web.prorata.client.ui.impl;

import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.prorata.client.presenter.impl.ProrataManagerPresenter;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectListPanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectProrataTreePanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectReportPanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProrataManagerPanel;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class ProrataClientModule extends AbstractGinModule
{

    @Override
    protected void configure()
    {
        bind(IProjectReportPanel.class).to(ProjectReportPanel.class);
        bind(IProrataManagerPanel.class).to(ProrataManagerPanel.class);
        bind(IProjectProrataTreePanel.class).to(ProjectProrataTreePanel.class);
        bind(IProjectListPanel.class).to(ProjectListPanel.class);

        bind(IProrataManagerPresenter.class).to(ProrataManagerPresenter.class).in(Singleton.class);
    }

}
