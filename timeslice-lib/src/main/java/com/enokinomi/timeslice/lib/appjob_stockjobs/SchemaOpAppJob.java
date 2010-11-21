package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;
import com.google.inject.Inject;

public class SchemaOpAppJob implements AppJob
{
    private final String jobId = "fix-schema-1";
    private final IConnectionContext connContext;
    private final ISchemaDetector schemaDetector;

    @Inject
    SchemaOpAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector)
    {
        this.connContext = connContext;
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
        return connContext.doWorkWithinContext(new ConnectionWork<String>()
        {
            @Override
            public String performWithConnection(Connection conn)
            {
                SchemaDuty upgradeDuty = new SchemaDuty("fix-schema-1.sql");

                upgradeDuty.createSchema(conn);

//                Integer versionPostUpgrade = detector.detectSchema(conn);

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
        });
    }
}
