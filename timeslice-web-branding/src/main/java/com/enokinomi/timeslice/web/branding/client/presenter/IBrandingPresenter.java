package com.enokinomi.timeslice.web.branding.client.presenter;

import com.enokinomi.timeslice.web.branding.client.core.BrandInfo;
import com.enokinomi.timeslice.web.core.client.util.Registration;

public interface IBrandingPresenter
{
    public interface IBrandingPresenterListener
    {
        void serverInfoRecieved(String info);
        void branded(BrandInfo brandInfo);
    }

    Registration addListener(IBrandingPresenterListener listener);

    void startGetBranding();
    void serverInfo();
}
