package com.enokinomi.timeslice.web.branding.client.presenter;

import com.enokinomi.timeslice.web.branding.client.core.BrandInfo;
import com.enokinomi.timeslice.web.branding.client.core.IBrandingSvcAsync;
import com.enokinomi.timeslice.web.core.client.util.ListenerManager;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class BrandingPresenter implements IBrandingPresenter
{
    private final IBrandingSvcAsync svc;

    private final ListenerManager<IBrandingPresenterListener> listenerMgr = new ListenerManager<IBrandingPresenterListener>();
    @Override public Registration addListener(IBrandingPresenterListener listener) { return listenerMgr.addListener(listener); }
    protected void fireBranded(BrandInfo brandInfo) { for (IBrandingPresenterListener l: listenerMgr.getListeners()) l.branded(brandInfo); }
    protected void fireServerInfoReceived(String serverInfo) { for (IBrandingPresenterListener l: listenerMgr.getListeners()) l.serverInfoRecieved(serverInfo); }

    @Inject
    public BrandingPresenter(IBrandingSvcAsync svc)
    {
        this.svc = svc;
    }

    @Override
    public void startGetBranding()
    {
        svc.getBrandInfo(new AsyncCallback<BrandInfo>()
        {
            @Override
            public void onSuccess(BrandInfo result)
            {
                fireBranded(result);
            }

            @Override
            public void onFailure(Throwable caught)
            {
            }
        });
    }

    @Override
    public void serverInfo()
    {
        svc.serverInfo(new AsyncCallback<String>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                fireServerInfoReceived("[server info not available]");
            }

            @Override
            public void onSuccess(String result)
            {
                fireServerInfoReceived(result);
            }
        });
    }

}
