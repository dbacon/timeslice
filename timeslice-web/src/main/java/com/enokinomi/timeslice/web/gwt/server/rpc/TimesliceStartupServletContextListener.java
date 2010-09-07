package com.enokinomi.timeslice.web.gwt.server.rpc;

import static com.enokinomi.timeslice.web.gwt.server.rpc.InitParamUtils.msgIfMissing;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.enokinomi.timeslice.app.assign.IAssignmentDao;
import com.enokinomi.timeslice.app.assign.INowProvider;
import com.enokinomi.timeslice.app.assign.TsSvcAssignmentDao;
import com.enokinomi.timeslice.app.core.Aggregate;
import com.enokinomi.timeslice.app.core.ITagStore;
import com.enokinomi.timeslice.app.core.ITimesliceStore;
import com.enokinomi.timeslice.app.core.Split;
import com.enokinomi.timeslice.app.core.Sum;
import com.enokinomi.timeslice.timeslice.ConnectionFactory;
import com.enokinomi.timeslice.timeslice.HsqldbTagStore;
import com.enokinomi.timeslice.timeslice.HsqldbTimesliceStore;
import com.enokinomi.timeslice.timeslice.IUserInfoDao;
import com.enokinomi.timeslice.timeslice.SchemaDetector;
import com.enokinomi.timeslice.timeslice.SchemaDuty;
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

        System.out.println("safe-dir  : " + safeDir);
        System.out.println("acl-file  : " + aclFilename);
        System.out.println("data-dir  : " + dataDir);

        IUserInfoDao userInfoDao = new UserInfoDao();
        SessionDataProvider sessionDataProvider = new SessionDataProvider(userInfoDao);
        SessionTracker sessionTracker = new SessionTracker(sessionDataProvider, aclFilename);

        ConnectionFactory connectionFactory = new ConnectionFactory();
        final Connection conn = connectionFactory.createConnection(dataDir + "/tsdb");
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    System.err.println("Closing database connection ...");
                    conn.close();
                    System.err.println("Database connection closed.");
                }
                catch (SQLException e)
                {
                    e.printStackTrace(System.err);
                }
            }
        });

        Integer schemaVersion = new SchemaDetector().detectSchema(conn);
        SchemaDuty schemaCreator = new SchemaDuty(1, "timeslice-1.ddl");
        if (schemaVersion < 0)
        {
            schemaCreator.createSchema(conn);
        }

        ITimesliceStore store = new HsqldbTimesliceStore(conn);
        ITagStore tagStore = new HsqldbTagStore(conn);

        Split splitter = new Split();
        Sum summer = new Sum();
        Aggregate aggregator = new Aggregate();

        TimesliceSvc timesliceSvc = new TimesliceSvc(store, summer, aggregator, splitter);

        AuthenticatedTimesliceSvc authenticatedTimesliceSvc = new AuthenticatedTimesliceSvc(timesliceSvc, sessionTracker);

        INowProvider nowProvider = new RealtimeNowProvider();
        IAssignmentDao assignmentDao = new TsSvcAssignmentDao(tagStore, nowProvider);
        AssignmentSvc assignmentSvc = new AssignmentSvc(assignmentDao, timesliceSvc, "");
        AssignmentSvcSession assignmentSvcSession = new AssignmentSvcSession(sessionTracker, assignmentSvc);


        sce.getServletContext().setAttribute(INJECTOR_SVC, new Injector(authenticatedTimesliceSvc, assignmentSvcSession));
    }

}
