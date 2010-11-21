package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;
import com.enokinomi.timeslice.lib.util.Mutable;
import com.google.inject.Inject;

public class DowngradeSchema1To0AppJob implements AppJob
{
    private final String jobId = "downgrade-data-1-0";
    private final IConnectionContext connContext;
    private final ISchemaDetector schemaDetector;

    @Inject
    DowngradeSchema1To0AppJob(IConnectionContext connContext, ISchemaDetector schemaDetector)
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
        final Mutable<Integer> versionPreUpgrade = new Mutable<Integer>(null);
        final Mutable<Integer> versionPostUpgrade = new Mutable<Integer>(null);

        connContext.doWorkWithinContext(new ConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                versionPreUpgrade.set(schemaDetector.detectSchema(conn));

                SchemaDuty upgradeDuty = new SchemaDuty("migration-sql-1-to-0.sql");

                upgradeDuty.createSchema(conn);

                versionPostUpgrade.set(schemaDetector.detectSchema(conn));

                return null; // Void
            }
        });

        return String.format("Upgrading result: version before -> after: %s -> %s", versionPreUpgrade.get(), versionPostUpgrade.get());
    }

}
