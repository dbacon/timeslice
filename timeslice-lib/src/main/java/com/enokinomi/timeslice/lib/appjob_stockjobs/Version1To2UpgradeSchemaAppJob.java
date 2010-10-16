package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Version1To2UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version1To2UpgradeSchemaAppJob(@Named("tsConnection") Connection conn, ISchemaDetector schemaDetector)
    {
        super("Upgrade data schema at version 1 to 2", conn, schemaDetector, 1, 2, "migration-sql-1-to-2.sql", null);
    }
}
