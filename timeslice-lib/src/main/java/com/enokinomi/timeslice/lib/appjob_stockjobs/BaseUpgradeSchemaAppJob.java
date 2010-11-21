package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.appjob.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;

public class BaseUpgradeSchemaAppJob implements AppJob
{
    private final String jobId;
    private final IConnectionContext connContext;
    private final ISchemaDetector schemaDetector;
    private final int existingVersionRequired;
    private final int targetVersion;
    private final String migrationSqlResourceName;
    private final String migrationDdl;

    BaseUpgradeSchemaAppJob(String jobId, IConnectionContext connContext, ISchemaDetector schemaDetector, int existingVersionRequired, int targetVersion, String migrationSqlResourceName, String migrationDdl)
    {
        this.jobId = jobId;
        this.connContext = connContext;
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
        return connContext.doWorkWithinContext(new ConnectionWork<String>()
        {
            @Override
            public String performWithConnection(Connection conn)
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
        });
    }

}
