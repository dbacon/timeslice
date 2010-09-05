package com.enokinomi.timeslice.web.gwt.server.rpc;

import static com.enokinomi.timeslice.web.gwt.server.rpc.InitParamUtils.msgIfMissing;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.enokinomi.timeslice.app.assign.IAssignmentDao;
import com.enokinomi.timeslice.app.assign.INowProvider;
import com.enokinomi.timeslice.app.assign.TsSvcAssignmentDao;
import com.enokinomi.timeslice.app.core.Aggregate;
import com.enokinomi.timeslice.app.core.ITimesliceStore;
import com.enokinomi.timeslice.app.core.Split;
import com.enokinomi.timeslice.app.core.StartTagIo;
import com.enokinomi.timeslice.app.core.Sum;
import com.enokinomi.timeslice.timeslice.IUserInfoDao;
import com.enokinomi.timeslice.timeslice.StoreManager;
import com.enokinomi.timeslice.timeslice.TimesliceApp;
import com.enokinomi.timeslice.timeslice.UserInfoDao;


public class TimesliceStartupServletContextListener implements ServletContextListener
{
    public static final String INJECTOR_SVC = "injectorSvc";

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        String aclFilename = msgIfMissing(sce.getServletContext(), "timeslice.acl", "INFO: No ACL filename specified, please set init-parameter 'timeslice.acl'.");
        String safeDir = msgIfMissing(sce.getServletContext(), "timeslice.safedir", "INFO: No safe-dir specified, please set init-parameter 'timeslice.safedir'.");
        String dataDir = msgIfMissing(sce.getServletContext(), "timeslice.datadir", "INFO: No data-dir available, not configuring any stores(use init-parameter 'timeslice.datadir' to specify).");
        String updateUrl = sce.getServletContext().getInitParameter("timeslice.updateurl");

        System.out.println("safe-dir  : " + safeDir);
        System.out.println("acl-file  : " + aclFilename);
        System.out.println("data-dir  : " + dataDir);

        IUserInfoDao userInfoDao = new UserInfoDao();
        SessionDataProvider sessionDataProvider = new SessionDataProvider(userInfoDao);
        SessionTracker sessionTracker = new SessionTracker(sessionDataProvider, aclFilename);

        List<ITimesliceStore> stores = new StoreManager(new File(dataDir),
                Arrays.asList(
                        new StoreManager.MemoryPlugin(),
                        new StoreManager.HsqlPlugin()
                )).configure();

        TimesliceApp timesliceApp = new TimesliceApp(safeDir, updateUrl, new StartTagIo(), new Split(), stores);

        Sum summer = new Sum();
        Aggregate aggregator = new Aggregate();

        TimesliceSvc timesliceSvc = new TimesliceSvc(timesliceApp, summer, aggregator);
        AuthenticatedTimesliceSvc authenticatedTimesliceSvc = new AuthenticatedTimesliceSvc(timesliceSvc, sessionTracker);

        INowProvider nowProvider = new RealtimeNowProvider();
        IAssignmentDao assignmentDao = new TsSvcAssignmentDao(timesliceApp, nowProvider);
        AssignmentSvc assignmentSvc = new AssignmentSvc(assignmentDao, timesliceSvc, "");
        AssignmentSvcSession assignmentSvcSession = new AssignmentSvcSession(sessionTracker, assignmentSvc);


        sce.getServletContext().setAttribute(INJECTOR_SVC, new Injector(authenticatedTimesliceSvc, assignmentSvcSession));
    }

}
