package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DetectSchemaVersionAppJob implements AppJob
{
    private final String jobId = "Show schema version";
    private final Connection conn;
    private final ISchemaDetector schemaDetector;

    @Inject
    DetectSchemaVersionAppJob(@Named("tsConnection") Connection conn, ISchemaDetector schemaDetector)
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
        String msg = "";

        try
        {
            msg = "detected version " + schemaDetector.detectSchema(conn);
        }
        catch (Exception e)
        {
            msg = "version detection failed: " + e.getMessage();
        }

        return String.format("Data schema version detection: " + msg);
    }

}
