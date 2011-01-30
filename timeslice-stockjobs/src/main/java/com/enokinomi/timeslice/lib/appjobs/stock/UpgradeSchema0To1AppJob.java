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

public class UpgradeSchema0To1AppJob implements AppJob
{
    private final String jobId = "Upgrade data schema at version 0 to 1";
    private final IConnectionContext connContext;
    private final ISchemaDetector schemaDetector;
    private final ISchemaDuty schemaDuty;

    @Inject
    UpgradeSchema0To1AppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty)
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
        return connContext.doWorkWithinContext(new IConnectionWork<String>()
        {
            @Override
            public String performWithConnection(Connection conn)
            {
                Integer versionPreUpgrade = schemaDetector.detectSchema(conn);

                try
                {
                    schemaDuty.createSchema(conn, IOUtils.toString(ClassLoader.getSystemResourceAsStream("migration-sql-0-to-1.sql")));
                }
                catch (IOException e)
                {
                    throw new RuntimeException("Could not upgrade: " + e.getMessage(), e);
                }

                Integer versionPostUpgrade = schemaDetector.detectSchema(conn);

                return String.format("Upgrading result: version before -> after: %s -> %s", versionPreUpgrade, versionPostUpgrade);
            }
        });
    }

}
