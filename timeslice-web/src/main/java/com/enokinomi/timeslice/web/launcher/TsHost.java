package com.enokinomi.timeslice.web.launcher;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import com.enokinomi.timeslice.lib.userinfo.TsSettings;
import com.enokinomi.timeslice.lib.userinfo.UserInfoDao;
import com.enokinomi.timeslice.lib.util.Check;
import com.google.inject.Inject;
import com.google.inject.internal.Nullable;
import com.google.inject.servlet.GuiceFilter;

/**
 * Prepares a webapp context and HTTP listener, then starts and awaits a server.
 *
 * @author dbacon
 *
 */
public class TsHost
{
    private static final int DefaultPort = 9080;

    private static final Logger log = Logger.getLogger(TsHost.class);

    private HandlerList handler;

    private final Server server = new Server();

    private final UserInfoDao userInfoDao;


    @Inject
    TsHost(@Nullable UserInfoDao userInfoDao)
    {
        this.userInfoDao = userInfoDao;
    }

    public void run(Integer port, String res)
    {
        if (null == port)
        {
            if (null != userInfoDao)
            {
                TsSettings systemSettings = userInfoDao.loadUserSettings("system", "system.server.");
                port = systemSettings.getScalar("system.server.port", DefaultPort);
                log.info("Queried port configuration: " + port);
            }

            port = Check.mapNullTo(port, 9080);
        }
        else
        {
            if (null != userInfoDao)
            {
                TsSettings newSettings = new TsSettings();
                newSettings.setConfScalar("system.server.port", port);
                userInfoDao.saveUserSettings("system", newSettings);
                log.info("Stored port configuration.");
            }
        }

        initServer(port);

        createGuiceContext("/", res);

        try
        {
            server.start();
        }
        catch (Exception e1)
        {
            throw new RuntimeException("Unexpected exception caught during server.start(): " + e1.getMessage(), e1);
        }

        try
        {
            server.join();
            log.info("Server exited unsolicited by TsHost.");
        }
        catch (InterruptedException e)
        {
            log.info("Host process caught interrupt, stopping the server.");

            // doesn't really work.  needs testing/fixing/axing.

            try
            {
                server.stop();
            }
            catch (Exception e1)
            {
                throw new RuntimeException("Unexpected exception caught during server.stop(): " + e1.getMessage(), e1);
            }

            try
            {
                server.join();
                log.info("Server exited cleanly.");
            }
            catch(InterruptedException e2)
            {
                log.warn("Force killed.");
            }
        }
    }

    private void initServer(int port)
    {
        Connector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector, });

        handler = new HandlerList();
        server.setHandler(handler);
    }

    public TsHost createGuiceContext(String contextPath, String resourceUrlOrFilename)
    {
        ServletContextHandler context = new ServletContextHandler(server, contextPath, true, false);
        try
        {
            context.setBaseResource(Resource.newResource(resourceUrlOrFilename));
        }
        catch(Exception e)
        {
            throw new RuntimeException("Wrapped checked-exception: " + e.getMessage(), e);
        }
        context.addServlet(new ServletHolder(new DefaultServlet()), "/");
        context.addFilter(new FilterHolder(new GuiceFilter()), "/*", 0);

        handler.addHandler(context);

        return this;
    }
}
