package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Version4To3DowngradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version4To3DowngradeSchemaAppJob(@Named("tsConnection") Connection conn, ISchemaDetector schemaDetector)
    {
        super(
                "Downgrade data schema at version 4 to 3",
                conn, schemaDetector, 4, 3,
                null,
                "drop table ts_version_4_done; " +
                "drop table ts_conf; " +
                "drop table ts_version_4;");
    }
}
