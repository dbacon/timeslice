package com.enokinomi.timeslice.launcher;

import java.sql.Connection;

import com.enokinomi.timeslice.timeslice.SchemaDetector;
import com.enokinomi.timeslice.timeslice.SchemaDuty;
import com.enokinomi.timeslice.web.gwt.server.rpc.AppJob;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SchemaOpAppJob implements AppJob
{
    private final String jobId = "fix-schema-1";
    private final Connection conn;

    @Inject
    public SchemaOpAppJob(@Named("tsConnection") Connection conn)
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
