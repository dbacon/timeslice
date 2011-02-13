package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.impl.VersionInvalidator;
import com.google.inject.Inject;

public class Version1To2UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version1To2UpgradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty, VersionInvalidator versionInvalidator)
    {
        super("Upgrade data schema at version 1 to 2", connContext, schemaDetector, schemaDuty, 1, 2, "migration-sql-1-to-2.sql", null, versionInvalidator);
    }
}
