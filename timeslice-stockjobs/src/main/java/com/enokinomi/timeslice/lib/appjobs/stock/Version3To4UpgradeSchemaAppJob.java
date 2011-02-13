package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.impl.VersionInvalidator;
import com.google.inject.Inject;

public class Version3To4UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version3To4UpgradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty, VersionInvalidator versionInvalidator)
    {
        super("Upgrade data schema at version 3 to 4", connContext, schemaDetector, schemaDuty, 3, 4, "migration-sql-3-to-4.sql", null, versionInvalidator);
    }
}
