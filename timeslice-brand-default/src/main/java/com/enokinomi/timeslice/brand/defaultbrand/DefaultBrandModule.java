package com.enokinomi.timeslice.brand.defaultbrand;

import com.enokinomi.timeslice.branding.api.BrandingAbstractModule;
import com.enokinomi.timeslice.branding.api.IBranding;


public class DefaultBrandModule extends BrandingAbstractModule
{
    public DefaultBrandModule()
    {
    }

    @Override
    protected void configureBrandModule()
    {
        bind(IBranding.class).to(DefaultBranding.class);
    }
}
