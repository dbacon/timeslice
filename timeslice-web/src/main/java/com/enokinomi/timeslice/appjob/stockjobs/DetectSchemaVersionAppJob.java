package com.enokinomi.timeslice.appjob.stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.SchemaDetector;
import com.enokinomi.timeslice.web.gwt.server.appjob.AppJob;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DetectSchemaVersionAppJob implements AppJob
{
    private final String jobId = "show-schema-version";
    private final Connection conn;

    @Inject
    DetectSchemaVersionAppJob(@Named("tsConnection") Connection conn)
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
        String msg = "";

        try
        {
            SchemaDetector detector = new SchemaDetector();
            msg = "detected version " + detector.detectSchema(conn);
        }
        catch (Exception e)
        {
            msg = "version detection failed: " + e.getMessage();
        }

        return String.format("Data schema version detection: " + msg);
    }

}
