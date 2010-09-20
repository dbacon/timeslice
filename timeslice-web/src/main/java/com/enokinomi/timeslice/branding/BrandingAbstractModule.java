package com.enokinomi.timeslice.branding;

import com.google.inject.AbstractModule;

public abstract class BrandingAbstractModule extends AbstractModule
{
    protected abstract void configureBrandModule();

    @Override
    protected void configure()
    {
        configureBrandModule();
    }
}
