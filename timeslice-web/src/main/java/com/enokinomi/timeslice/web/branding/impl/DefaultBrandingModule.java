package com.enokinomi.timeslice.web.branding.impl;

import com.enokinomi.timeslice.web.branding.api.BrandingAbstractModule;
import com.enokinomi.timeslice.web.branding.api.IBranding;


public class DefaultBrandingModule extends BrandingAbstractModule
{
    @Override
    protected void configureBrandModule()
    {
        bind(IBranding.class).to(DefaultBranding.class);
    }
}
