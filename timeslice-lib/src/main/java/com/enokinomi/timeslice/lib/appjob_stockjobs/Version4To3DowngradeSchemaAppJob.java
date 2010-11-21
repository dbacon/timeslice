package com.enokinomi.timeslice.lib.appjob_stockjobs;

import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;

public class Version4To3DowngradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version4To3DowngradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector)
    {
        super(
                "Downgrade data schema at version 4 to 3",
                connContext, schemaDetector, 4, 3,
                null,
                "drop table ts_version_4_done; " +
                "drop table ts_conf; " +
                "drop table ts_version_4;");
    }
}
