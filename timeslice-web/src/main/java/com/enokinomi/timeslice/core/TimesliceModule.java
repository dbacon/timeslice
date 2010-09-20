package com.enokinomi.timeslice.core;

import java.sql.Connection;
import java.sql.DriverManager;

import com.enokinomi.timeslice.lib.assign.HsqldbTagStore;
import com.enokinomi.timeslice.lib.assign.INowProvider;
import com.enokinomi.timeslice.lib.assign.ITagStore;
import com.enokinomi.timeslice.lib.assign.RealtimeNowProvider;
import com.enokinomi.timeslice.lib.task.HsqldbTimesliceStore;
import com.enokinomi.timeslice.lib.task.ITimesliceStore;
import com.enokinomi.timeslice.lib.userinfo.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.UserInfoDao;
import com.enokinomi.timeslice.web.gwt.client.appjob.core.IAppJobSvc;
import com.enokinomi.timeslice.web.gwt.client.assigned.core.IAssignmentSvc;
import com.enokinomi.timeslice.web.gwt.client.task.core.ITimesliceSvc;
import com.enokinomi.timeslice.web.gwt.server.appjob.AppJobSvc;
import com.enokinomi.timeslice.web.gwt.server.assigned.AssignmentSvcSession;
import com.enokinomi.timeslice.web.gwt.server.task.AuthenticatedTimesliceSvc;
import com.enokinomi.timeslice.web.gwt.server.task.SessionDataProvider;
import com.enokinomi.timeslice.web.gwt.server.task.SessionTracker;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public final class TimesliceModule extends AbstractModule
{
    private final String dbFilename;
    private final String aclFilename;

    public TimesliceModule(String dbFilename, String aclFilename)
    {
        this.dbFilename = dbFilename;
        this.aclFilename = aclFilename;
    }

    @Override
    protected void configure()
    {
        bind(ITimesliceSvc.class).to(AuthenticatedTimesliceSvc.class);
        bind(String.class).annotatedWith(Names.named("acl")).toInstance(aclFilename);
        bind(String.class).annotatedWith(Names.named("schemaResource")).toInstance("timeslice-1.ddl");
        bind(SessionTracker.class).in(Scopes.SINGLETON);
        bind(ITimesliceStore.class).to(HsqldbTimesliceStore.class).asEagerSingleton();
        bind(SessionDataProvider.class);
        bind(IUserInfoDao.class).to(UserInfoDao.class);
        bind(Connection.class).annotatedWith(Names.named("tsConnection")).to(Connection.class);

        bind(IAssignmentSvc.class).to(AssignmentSvcSession.class);
        bind(String.class).annotatedWith(Names.named("assignDefault")).toInstance("");
        bind(INowProvider.class).to(RealtimeNowProvider.class);
        bind(ITagStore.class).to(HsqldbTagStore.class);

        bind(IAppJobSvc.class).to(AppJobSvc.class);
    }

    @Provides Connection getConnection()
    {
        try
        {
            Class.forName("org.hsqldb.jdbcDriver");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:" + dbFilename + ";shutdown=true;", "SA", "");
//                    conn.setAutoCommit(false);
            return conn;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Wrapped checked-exception: " + e.getMessage(), e);
        }
    }
}
