package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.google.inject.Inject;

public class Version2To3UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version2To3UpgradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty)
    {
        super("Upgrade data schema at version 2 to 3", connContext, schemaDetector, schemaDuty, 2, 3, "migration-sql-2-to-3.sql", null);
    }
}
