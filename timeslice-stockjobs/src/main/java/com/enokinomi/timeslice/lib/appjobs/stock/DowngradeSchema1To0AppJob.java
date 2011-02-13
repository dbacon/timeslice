package com.enokinomi.timeslice.lib.appjobs.stock;

import java.io.IOException;
import java.sql.Connection;

import org.apache.commons.io.IOUtils;

import com.enokinomi.timeslice.lib.appjob.api.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.util.Mutable;
import com.google.inject.Inject;

public class DowngradeSchema1To0AppJob implements AppJob
{
    private final String jobId = "downgrade-data-1-0";
    private final IConnectionContext connContext;
    private final ISchemaDetector schemaDetector;
    private final ISchemaDuty schemaDuty;

    @Inject
    DowngradeSchema1To0AppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty)
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
        final Mutable<Integer> versionPreUpgrade = new Mutable<Integer>(null);
        final Mutable<Integer> versionPostUpgrade = new Mutable<Integer>(null);

        connContext.doWorkWithinWritableContext(new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                versionPreUpgrade.set(schemaDetector.detectSchema(conn));

                try
                {
                    schemaDuty.createSchema(conn, IOUtils.toString(ClassLoader.getSystemResourceAsStream("migration-sql-1-to-0.sql")));
                }
                catch (IOException e)
                {
                    throw new RuntimeException("Could not downgrade: " + e.getMessage(), e);
                }

                versionPostUpgrade.set(schemaDetector.detectSchema(conn));

                return null; // Void
            }
        });

        return String.format("Upgrading result: version before -> after: %s -> %s", versionPreUpgrade.get(), versionPostUpgrade.get());
    }

}
