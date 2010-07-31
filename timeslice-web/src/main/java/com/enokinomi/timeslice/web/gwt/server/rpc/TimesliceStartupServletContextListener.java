package bacond.timeslice.web.gwt.server.rpc;

import static bacond.timeslice.web.gwt.server.rpc.InitParamUtils.msgIfMissing;
import static bacond.timeslice.web.gwt.server.rpc.InitParamUtils.parseIntegerOrDefault;

import java.io.File;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import bacond.timeslicer.app.core.Split;
import bacond.timeslicer.app.core.StartTagIo;
import bacond.timeslicer.timeslice.StoreManager;
import bacond.timeslicer.timeslice.TimesliceApp;

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
        String updateUrl = context.getInitParameter("timeslice.updateurl");
        Integer tzOffset = parseIntegerOrDefault(context, "timeslice.tzoffset", 0);

        TimesliceApp ta = new TimesliceApp(aclFilename, safeDir, updateUrl, tzOffset, new StartTagIo(), new Split());

        String dataDir = msgIfMissing(context, "timeslice.datadir", "INFO: No data-dir available, not configuring any stores(use init-parameter 'timeslice.datadir' to specify).");
        if (null != dataDir) configureStores(ta, dataDir);

        System.out.println("safe-dir  : " + ta.getSafeDir());
        System.out.println("acl-file  : " + ta.getAclFileName());
        System.out.println("tz-offset : " + ta.getTzOffset());
        System.out.println("data-dir  : " + dataDir);

        return ta;
    }

    private void configureStores(TimesliceApp ta, String dataDir)
    {
        try
        {
            new StoreManager(new File(dataDir),
                    Arrays.asList(
                            new StoreManager.MemoryPlugin(),
                            new StoreManager.HsqlPlugin()
                            )).configure(ta);
        }
        catch (RuntimeException e)
        {
            System.out.println("WARNING: store manager failed to setup stores: " + e.getMessage());
        }
    }

}
