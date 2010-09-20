package com.enokinomi.timeslice.core;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import com.enokinomi.timeslice.web.gwt.client.appjob.core.IAppJobSvc;
import com.enokinomi.timeslice.web.gwt.client.assigned.core.IAssignmentSvc;
import com.enokinomi.timeslice.web.gwt.client.task.core.ITimesliceSvc;
import com.enokinomi.timeslice.web.gwt.server.appjob.AppJobSvc;
import com.enokinomi.timeslice.web.gwt.server.assigned.AssignmentSvcSession;
import com.enokinomi.timeslice.web.gwt.server.task.TimesliceSvcSession;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

public final class TimesliceWebModule extends AbstractModule
{
    private final String dbFilename;
    private final String aclFilename;
    private final String safeDir;

    public TimesliceWebModule(String dbFilename, String aclFilename, String safeDir)
    {
        this.dbFilename = dbFilename;
        this.aclFilename = aclFilename;
        this.safeDir = safeDir;
    }

    @Override
    protected void configure()
    {
        bind(ITimesliceSvc.class).to(TimesliceSvcSession.class);

        bind(String.class).annotatedWith(Names.named("acl")).toInstance(aclFilename);
        bind(String.class).annotatedWith(Names.named("schemaResource")).toInstance("timeslice-1.ddl");
        bind(Connection.class).annotatedWith(Names.named("tsConnection")).to(Connection.class);

        bind(IAssignmentSvc.class).to(AssignmentSvcSession.class);
        bind(String.class).annotatedWith(Names.named("assignDefault")).toInstance("");

        bind(IAppJobSvc.class).to(AppJobSvc.class);

        bind(File.class).annotatedWith(Names.named("safeDir")).toInstance(new File(safeDir));
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
