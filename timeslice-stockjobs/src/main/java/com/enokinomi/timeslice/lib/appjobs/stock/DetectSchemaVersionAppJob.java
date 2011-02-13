package com.enokinomi.timeslice.lib.appjobs.stock;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.api.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.google.inject.Inject;

public class DetectSchemaVersionAppJob implements AppJob
{
    private final String jobId = "Show schema version";
    private final IConnectionContext connContext;
    private final ISchemaDetector schemaDetector;

    @Inject
    DetectSchemaVersionAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector)
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
        String msg = "";

        try
        {
            msg = connContext.doWorkWithinWritableContext(new IConnectionWork<String>()
            {
                @Override
                public String performWithConnection(Connection conn)
                {
                    return "detected version " + schemaDetector.detectSchema(conn);
                }
            });
        }
        catch (Exception e)
        {
            msg = "version detection failed: " + e.getMessage();
        }

        return String.format("Data schema version detection: " + msg);
    }

}
