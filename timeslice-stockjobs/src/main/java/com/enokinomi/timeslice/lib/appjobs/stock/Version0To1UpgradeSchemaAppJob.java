package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.impl.VersionInvalidator;
import com.google.inject.Inject;

public class Version0To1UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version0To1UpgradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty, VersionInvalidator versionInvalidator)
    {
        super("Upgrade data schema at version 0 to 1", connContext, schemaDetector, schemaDuty, 0, 1, "migration-sql-0-to-1.sql", null, versionInvalidator);
    }
}
