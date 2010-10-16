package com.enokinomi.timeslice.web.branding.api;

import com.google.inject.AbstractModule;

public abstract class BrandingAbstractModule extends AbstractModule
{
    protected abstract void configureBrandModule();

    @Override
    protected final void configure()
    {
        configureBrandModule();
    }
}
