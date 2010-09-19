package com.enokinomi.timeslice.launcher;


import com.enokinomi.timeslice.web.gwt.server.rpc.GuiceRpcService;
import com.google.inject.Guice;
import com.google.inject.servlet.ServletModule;


public class Driver
{
    /**
     * Any argument present is taken as the name of settings to be loaded,
     * each having higher precedence than the previous.  System properties
     * are loaded and have the lowest precedence.
     *
     * <p>
     * Properties queried by TsHost are:<ul>
     *   <li><code>timeslice.port</code> - port on which the HTTP listener should listen. </li>
     *   <li><code>timeslice.war</code> - WAR file which the container should expand and deploy/host.</li>
     * </ul>
     * </p>
     *
     * <p>
     * Any property with the name beginning with <code>timeslice.</code> will be passed
     * as init-params to the container context.
     * </p>
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)
    {
        TsHostSettingsManager sm = new TsHostSettingsManager(System.getProperties()).pushSettings(args, true);

        Guice.createInjector(
                new ServletModule()
                {
                    @Override
                    protected void configureServlets()
                    {
                        serve("/timeslice.App/gwtrpc").with(GuiceRpcService.class);
                    }
                },
                new TimesliceModule(sm.getDatabaseBasePath(), sm.getAclFilename())
            );

        new TsHost(sm.getPort())
            .createGuiceContext("/", sm.getResourceUrlOrFilename())
            .run();
    }
}
