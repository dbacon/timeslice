package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.google.inject.Inject;

public class Version3To2DowngradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version3To2DowngradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty)
    {
        super(
                "Downgrade data schema at version 3 to 2",
                connContext, schemaDetector, schemaDuty, 3, 2,
                null,
                "drop table ts_version_3_done; " +
                "drop table ts_ordering; " +
                "drop table ts_version_3;");
    }
}
