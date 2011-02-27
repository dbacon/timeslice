package com.enokinomi.timeslice.web.prorata.client.presenter.impl;

import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.google.gwt.inject.client.AbstractGinModule;

public class ProrataPresenterClientModule extends AbstractGinModule
{

    @Override
    protected void configure()
    {
        bind(IProrataManagerPresenter.class).to(ProrataManagerPresenter.class);
    }

}
