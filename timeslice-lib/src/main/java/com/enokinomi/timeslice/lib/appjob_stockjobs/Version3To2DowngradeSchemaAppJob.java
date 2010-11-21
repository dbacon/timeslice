package com.enokinomi.timeslice.lib.appjob_stockjobs;

import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;

public class Version3To2DowngradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version3To2DowngradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector)
    {
        super(
                "Downgrade data schema at version 3 to 2",
                connContext, schemaDetector, 3, 2,
                null,
                "drop table ts_version_3_done; " +
                "drop table ts_ordering; " +
                "drop table ts_version_3;");
    }
}
