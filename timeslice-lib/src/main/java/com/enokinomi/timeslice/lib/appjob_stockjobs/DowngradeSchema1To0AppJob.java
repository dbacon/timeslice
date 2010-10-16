package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DowngradeSchema1To0AppJob implements AppJob
{
    private final String jobId = "downgrade-data-1-0";
    private final Connection conn;
    private final ISchemaDetector schemaDetector;

    @Inject
    DowngradeSchema1To0AppJob(@Named("tsConnection") Connection conn, ISchemaDetector schemaDetector)
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
        Integer versionPreUpgrade = schemaDetector.detectSchema(conn);

        SchemaDuty upgradeDuty = new SchemaDuty("migration-sql-1-to-0.sql");

        upgradeDuty.createSchema(conn);

        Integer versionPostUpgrade = schemaDetector.detectSchema(conn);

        return String.format("Upgrading result: version before -> after: %s -> %s", versionPreUpgrade, versionPostUpgrade);
    }

}
