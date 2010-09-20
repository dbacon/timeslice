package com.enokinomi.timeslice.appjob.stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.SchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;
import com.enokinomi.timeslice.web.gwt.server.appjob.AppJob;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SchemaOpAppJob implements AppJob
{
    private final String jobId = "fix-schema-1";
    private final Connection conn;

    @Inject
    SchemaOpAppJob(@Named("tsConnection") Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public String getJobId()
    {
        return jobId;
    }

    @Override
    public String perform()
    {

        SchemaDuty upgradeDuty = new SchemaDuty("fix-schema-1.sql");

        upgradeDuty.createSchema(conn);

//        Integer versionPostUpgrade = detector.detectSchema(conn);

        String msg = "";
        try
        {
            SchemaDetector detector = new SchemaDetector();
            msg = "version detected post-operation: " + detector.detectSchema(conn);
        }
        catch (Exception e)
        {
            msg = "version detection failed: " + e.getMessage();
        }

        return String.format("Schema operation: " + msg);
    }

}
