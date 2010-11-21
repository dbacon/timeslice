package com.enokinomi.timeslice.lib.appjob_stockjobs;

import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;

public class Version1To2UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version1To2UpgradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector)
    {
        super("Upgrade data schema at version 1 to 2", connContext, schemaDetector, 1, 2, "migration-sql-1-to-2.sql", null);
    }
}
