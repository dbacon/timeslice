package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.google.inject.Inject;

public class Version4To3DowngradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version4To3DowngradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty)
    {
        super(
                "Downgrade data schema at version 4 to 3",
                connContext, schemaDetector, schemaDuty, 4, 3,
                null,
                "drop table ts_version_4_done; " +
                "drop table ts_conf; " +
                "drop table ts_version_4;");
    }
}
