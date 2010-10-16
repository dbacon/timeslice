package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;

public class BaseUpgradeSchemaAppJob implements AppJob
{
    private final String jobId;
    private final Connection conn;
    private final ISchemaDetector schemaDetector;
    private final int existingVersionRequired;
    private final int targetVersion;
    private final String migrationSqlResourceName;
    private final String migrationDdl;

    BaseUpgradeSchemaAppJob(String jobId, Connection conn, ISchemaDetector schemaDetector, int existingVersionRequired, int targetVersion, String migrationSqlResourceName, String migrationDdl)
    {
        this.jobId = jobId;
        this.conn = conn;
        this.schemaDetector = schemaDetector;
        this.existingVersionRequired = existingVersionRequired;
        this.targetVersion = targetVersion;
        this.migrationSqlResourceName = migrationSqlResourceName;
        this.migrationDdl = migrationDdl;
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

        if (versionPreUpgrade != existingVersionRequired)
        {
            throw new RuntimeException("Current schema must be version '" + existingVersionRequired + "' for this upgrade - found version " + versionPreUpgrade);
        }

        SchemaDuty upgradeDuty = new SchemaDuty(migrationSqlResourceName);

        if (null != migrationSqlResourceName)
        {
            upgradeDuty.createSchema(conn);
        }

        if (null != migrationDdl)
        {
            upgradeDuty.createSchema(conn, migrationDdl);
        }

        Integer versionPostUpgrade = schemaDetector.detectSchema(conn);

        if (versionPostUpgrade != targetVersion)
        {
            throw new RuntimeException("Expected schema version post-upgrade to be '" + targetVersion + "' but found '" + versionPostUpgrade + "'");
        }

        return String.format("Upgrading result: version before -> after: %s -> %s", versionPreUpgrade, versionPostUpgrade);
    }

}
