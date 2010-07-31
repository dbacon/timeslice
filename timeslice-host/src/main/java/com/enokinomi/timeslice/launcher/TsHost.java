package com.enokinomi.timeslice.launcher;

import java.io.File;
import java.util.Map;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Prepares a webapp context and HTTP listener, then starts and awaits a server.
 *
 * @author dbacon
 *
 */
public class TsHost
{
    private static final Logger log = Log.getLogger(TsHost.class.getName());

    private final int port;

    private HandlerList handler;

    private final Server server = new Server();


    public TsHost(int port)
    {
        this.port = port;

        initServer();
    }

    public void run()
    {
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
            System.out.println("Server exited unsolicted by TsHost.");
        }
        catch (InterruptedException e)
        {
            // doesn't really work.  needs testing/fixing/axing.

            System.out.println("Host process caught interrupt, stopping the server.");
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
                System.out.println("Server exited cleanly.");
            }
            catch(InterruptedException e2)
            {
                System.out.println("Force killed.");
            }
        }
    }

    private void initServer()
    {
        Connector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector, });

        handler = new HandlerList();
        server.setHandler(handler);
    }

    public TsHost createWarHandler(String warFileName, String contextPath, Map<String, String> initParams)
    {
        // do a quick pre-check on the war-file
        File f = new File(warFileName);
        if (!f.canRead() || !f.isFile())
        {
            log.warn("Could not find a decent war(not a file, or no permissions to read): '" + warFileName + "'");
            return this;
        }

        WebAppContext context = new WebAppContext();
        context.setWar(warFileName);
        context.setContextPath(contextPath);
        context.setInitParams(initParams);

        handler.addHandler(context);

        return this;
    }
}
