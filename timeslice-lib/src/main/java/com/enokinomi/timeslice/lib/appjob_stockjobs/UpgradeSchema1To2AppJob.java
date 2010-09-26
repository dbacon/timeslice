package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class UpgradeSchema1To2AppJob implements AppJob
{
    private final String jobId = "upgrade-data-1-2";
    private final Connection conn;

    @Inject
    UpgradeSchema1To2AppJob(@Named("tsConnection") Connection conn)
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
        SchemaDetector detector = new SchemaDetector();
        Integer versionPreUpgrade = detector.detectSchema(conn);

        if (versionPreUpgrade != 1) throw new RuntimeException("Current schema must be version 1 for this upgrade - found version " + versionPreUpgrade);

        SchemaDuty upgradeDuty = new SchemaDuty("migration-sql-1-to-2.sql");

        upgradeDuty.createSchema(conn);

        Integer versionPostUpgrade = detector.detectSchema(conn);

        return String.format("Upgrading result: version before -> after: %s -> %s", versionPreUpgrade, versionPostUpgrade);
    }

}
