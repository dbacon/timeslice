package com.enokinomi.timeslice.lib.appjob_stockjobs;

import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;

public class Version3To4UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version3To4UpgradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector)
    {
        super("Upgrade data schema at version 3 to 4", connContext, schemaDetector, 3, 4, "migration-sql-3-to-4.sql", null);
    }
}
