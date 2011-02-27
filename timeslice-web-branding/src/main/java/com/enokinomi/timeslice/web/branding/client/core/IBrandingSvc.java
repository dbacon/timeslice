package com.enokinomi.timeslice.web.branding.client.core;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtrpc")
public interface IBrandingSvc extends RemoteService
{
    String serverInfo();
    BrandInfo getBrandInfo();
}
