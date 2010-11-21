package com.enokinomi.timeslice.lib.appjob_stockjobs;

import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;

public class Version2To3UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version2To3UpgradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector)
    {
        super("Upgrade data schema at version 2 to 3", connContext, schemaDetector, 2, 3, "migration-sql-2-to-3.sql", null);
    }
}
