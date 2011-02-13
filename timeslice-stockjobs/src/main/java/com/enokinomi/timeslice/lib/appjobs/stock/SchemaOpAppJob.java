package com.enokinomi.timeslice.lib.appjobs.stock;

import java.io.IOException;
import java.sql.Connection;

import org.apache.commons.io.IOUtils;

import com.enokinomi.timeslice.lib.appjob.api.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.google.inject.Inject;

public class SchemaOpAppJob implements AppJob
{
    private final String jobId = "fix-schema-1";
    private final IConnectionContext connContext;
    private final ISchemaDetector schemaDetector;
    private final ISchemaDuty schemaDuty;

    @Inject
    SchemaOpAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty)
    {
        this.connContext = connContext;
        this.schemaDetector = schemaDetector;
        this.schemaDuty = schemaDuty;
    }

    @Override
    public String getJobId()
    {
        return jobId;
    }

    @Override
    public String perform()
    {
        return connContext.doWorkWithinWritableContext(new IConnectionWork<String>()
        {
            @Override
            public String performWithConnection(Connection conn)
            {
                try
                {
                    schemaDuty.createSchema(conn, IOUtils.toString(ClassLoader.getSystemResourceAsStream("fix-schema-1.sql")));
                }
                catch (IOException e)
                {
                    throw new RuntimeException("Schema-op failed: " + e.getMessage(), e);
                }

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
