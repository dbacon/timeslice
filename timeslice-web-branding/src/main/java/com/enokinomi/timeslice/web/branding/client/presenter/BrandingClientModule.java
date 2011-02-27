package com.enokinomi.timeslice.web.branding.client.presenter;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

public class BrandingClientModule extends AbstractGinModule
{

    @Override
    protected void configure()
    {
        bind(IBrandingPresenter.class).to(BrandingPresenter.class).in(Singleton.class);
    }

}
