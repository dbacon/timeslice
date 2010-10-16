package com.enokinomi.timeslice.lib.appjob_stockjobs;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.ISchemaDetector;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Version2To3UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version2To3UpgradeSchemaAppJob(@Named("tsConnection") Connection conn, ISchemaDetector schemaDetector)
    {
        super("Upgrade data schema at version 2 to 3", conn, schemaDetector, 2, 3, "migration-sql-2-to-3.sql", null);
    }
}
