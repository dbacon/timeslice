package com.enokinomi.timeslice.web.branding.client.core;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IBrandingSvcAsync
{
    void serverInfo(AsyncCallback<String> callback);
    void getBrandInfo(AsyncCallback<BrandInfo> callback);
}
