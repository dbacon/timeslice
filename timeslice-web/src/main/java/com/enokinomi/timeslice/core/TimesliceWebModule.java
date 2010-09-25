package com.enokinomi.timeslice.core;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.enokinomi.timeslice.web.gwt.client.appjob.core.IAppJobSvc;
import com.enokinomi.timeslice.web.gwt.client.assigned.core.IAssignmentSvc;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvc;
import com.enokinomi.timeslice.web.gwt.client.task.core.ITimesliceSvc;
import com.enokinomi.timeslice.web.gwt.server.appjob.AppJobSvc;
import com.enokinomi.timeslice.web.gwt.server.assigned.AssignmentSvcSession;
import com.enokinomi.timeslice.web.gwt.server.prorata.ProRataSvc;
import com.enokinomi.timeslice.web.gwt.server.task.TimesliceSvcSession;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

public final class TimesliceWebModule extends AbstractModule
{
    private static final Logger log = Logger.getLogger(TimesliceWebModule.class);

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
        bind(IProRataSvc.class).to(ProRataSvc.class);

        bind(File.class).annotatedWith(Names.named("safeDir")).toInstance(new File(safeDir));
    }

    @Provides Connection getConnection()
    {
        try
        {
            Class.forName("org.hsqldb.jdbcDriver");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:" + dbFilename + ";shutdown=true;", "SA", "");

            Statement st = conn.createStatement();
            st.execute("SET WRITE_DELAY 500 MILLIS");
            st.close();
            log.debug("Set write-delay to 500ms");

            return conn;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Wrapped checked-exception: " + e.getMessage(), e);
        }
    }
}
