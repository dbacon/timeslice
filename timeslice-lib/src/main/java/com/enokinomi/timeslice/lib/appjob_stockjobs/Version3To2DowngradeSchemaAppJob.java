package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Version3To2DowngradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version3To2DowngradeSchemaAppJob(@Named("tsConnection") Connection conn, ISchemaDetector schemaDetector)
    {
        super(
                "Downgrade data schema at version 3 to 2",
                conn, schemaDetector, 3, 2,
                null,
                "drop table ts_version_3_done; " +
                "drop table ts_conf; " +
                "drop table ts_ordering; " +
                "drop table ts_version_3;");
    }
}
