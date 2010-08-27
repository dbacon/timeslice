package com.enokinomi.timeslice.web.gwt.server.rpc;

import static com.enokinomi.timeslice.web.gwt.server.rpc.InitParamUtils.msgIfMissing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.enokinomi.timeslice.app.core.ITimesliceStore;
import com.enokinomi.timeslice.app.core.Split;
import com.enokinomi.timeslice.app.core.StartTagIo;
import com.enokinomi.timeslice.timeslice.StoreManager;
import com.enokinomi.timeslice.timeslice.TimesliceApp;


public class TimesliceStartupServletContextListener implements ServletContextListener
{
    public static final String SCK_TA = "timesliceapp";

    public static TimesliceApp getTimesliceApp(ServletContext context)
    {
        return (TimesliceApp) context.getAttribute(SCK_TA);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        sce.getServletContext().setAttribute(SCK_TA, createTimesliceApp(sce.getServletContext()));
    }

    private TimesliceApp createTimesliceApp(ServletContext context)
    {
        String aclFilename = msgIfMissing(context, "timeslice.acl", "INFO: No ACL filename specified, please set init-parameter 'timeslice.acl'.");
        String safeDir = msgIfMissing(context, "timeslice.safedir", "INFO: No safe-dir specified, please set init-parameter 'timeslice.safedir'.");
        String dataDir = msgIfMissing(context, "timeslice.datadir", "INFO: No data-dir available, not configuring any stores(use init-parameter 'timeslice.datadir' to specify).");
        String updateUrl = context.getInitParameter("timeslice.updateurl");

        System.out.println("safe-dir  : " + safeDir);
        System.out.println("acl-file  : " + aclFilename);
        System.out.println("data-dir  : " + dataDir);

        List<ITimesliceStore> stores = configureStores(dataDir);

        TimesliceApp ta = new TimesliceApp(aclFilename, safeDir, updateUrl, new StartTagIo(), new Split(), stores);

        return ta;
    }

    private ArrayList<ITimesliceStore> configureStores(String dataDir)
    {
        return new StoreManager(new File(dataDir),
                Arrays.asList(
                        new StoreManager.MemoryPlugin(),
                        new StoreManager.HsqlPlugin()
                )).configure();
    }

}
