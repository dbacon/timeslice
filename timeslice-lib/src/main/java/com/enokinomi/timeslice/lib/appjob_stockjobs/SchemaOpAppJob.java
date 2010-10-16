package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SchemaOpAppJob implements AppJob
{
    private final String jobId = "fix-schema-1";
    private final Connection conn;
    private final ISchemaDetector schemaDetector;

    @Inject
    SchemaOpAppJob(@Named("tsConnection") Connection conn, ISchemaDetector schemaDetector)
    {
        this.conn = conn;
        this.schemaDetector = schemaDetector;
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
            msg = "version detected post-operation: " + schemaDetector.detectSchema(conn);
        }
        catch (Exception e)
        {
            msg = "version detection failed: " + e.getMessage();
        }

        return String.format("Schema operation: " + msg);
    }

}
