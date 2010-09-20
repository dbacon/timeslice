package com.enokinomi.timeslice.branding;


public class DefaultBrandingModule extends BrandingAbstractModule
{
    @Override
    protected void configureBrandModule()
    {
        bind(IBranding.class).to(DefaultBranding.class);
    }
}
