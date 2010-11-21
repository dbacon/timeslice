package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;
import com.google.inject.Inject;

public class UpgradeSchema0To1AppJob implements AppJob
{
    private final String jobId = "Upgrade data schema at version 0 to 1";
    private final IConnectionContext connContext;
    private final ISchemaDetector schemaDetector;

    @Inject
    UpgradeSchema0To1AppJob(IConnectionContext connContext, ISchemaDetector schemaDetector)
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
                Integer versionPreUpgrade = schemaDetector.detectSchema(conn);

                SchemaDuty upgradeDuty = new SchemaDuty("migration-sql-0-to-1.sql");

                upgradeDuty.createSchema(conn);

                Integer versionPostUpgrade = schemaDetector.detectSchema(conn);

                return String.format("Upgrading result: version before -> after: %s -> %s", versionPreUpgrade, versionPostUpgrade);
            }
        });
    }

}
