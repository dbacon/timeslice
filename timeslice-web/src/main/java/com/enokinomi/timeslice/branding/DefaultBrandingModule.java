package com.enokinomi.timeslice.launcher;


public class DefaultBrandingModule extends BrandingAbstractModule
{
    @Override
    protected void configureBrandModule()
    {
        bind(IBranding.class).to(DefaultBranding.class);
    }
}
