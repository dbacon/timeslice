package com.enokinomi.timeslice.launcher;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TsWebLaunchModule extends AbstractModule
{
    private final Integer port;
    private final String res;

    TsWebLaunchModule(Integer port, String res)
    {
        this.port = port;
        this.res = res;
    }

    @Override
    protected void configure()
    {
    }

    @Provides TsHost createTsHost()
    {
        return new TsHost(port)
            .createGuiceContext("/", res);
    }
}
