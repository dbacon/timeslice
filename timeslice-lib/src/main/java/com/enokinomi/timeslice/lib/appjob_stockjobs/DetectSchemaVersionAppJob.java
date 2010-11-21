package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
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
            msg = connContext.doWorkWithinContext(new ConnectionWork<String>()
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
