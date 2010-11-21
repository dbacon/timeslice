package com.enokinomi.timeslice.lib.appjobs.stock;

import java.io.IOException;
import java.sql.Connection;

import org.apache.commons.io.IOUtils;

import com.enokinomi.timeslice.lib.appjob.api.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;

public class BaseUpgradeSchemaAppJob implements AppJob
{
    private final String jobId;
    private final IConnectionContext connContext;
    private final ISchemaDetector schemaDetector;
    private final int existingVersionRequired;
    private final int targetVersion;
    private final String migrationSqlResourceName;
    private final String migrationDdl;
    private final ISchemaDuty schemaDuty;

    BaseUpgradeSchemaAppJob(String jobId, IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty, int existingVersionRequired, int targetVersion, String migrationSqlResourceName, String migrationDdl)
    {
        this.jobId = jobId;
        this.connContext = connContext;
        this.schemaDetector = schemaDetector;
        this.schemaDuty = schemaDuty;
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
        return connContext.doWorkWithinContext(new IConnectionWork<String>()
        {
            @Override
            public String performWithConnection(Connection conn)
            {
                Integer versionPreUpgrade = schemaDetector.detectSchema(conn);

                if (versionPreUpgrade != existingVersionRequired)
                {
                    throw new RuntimeException("Current schema must be version '" + existingVersionRequired + "' for this upgrade - found version " + versionPreUpgrade);
                }

                if (null != migrationSqlResourceName)
                {
                    try
                    {
                        schemaDuty.createSchema(conn, IOUtils.toString(ClassLoader.getSystemResourceAsStream(migrationSqlResourceName)));
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("Could not execute migration sql resource '" + migrationSqlResourceName + "': " + e.getMessage(), e);
                    }
                }

                if (null != migrationDdl)
                {
                    schemaDuty.createSchema(conn, migrationDdl);
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
