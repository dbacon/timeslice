package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Version3To4UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version3To4UpgradeSchemaAppJob(@Named("tsConnection") Connection conn, ISchemaDetector schemaDetector)
    {
        super("Upgrade data schema at version 3 to 4", conn, schemaDetector, 3, 4, "migration-sql-3-to-4.sql", null);
    }
}
