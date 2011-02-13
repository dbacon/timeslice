package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.impl.VersionInvalidator;
import com.google.inject.Inject;

public class Version5To4DowngradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version5To4DowngradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty, VersionInvalidator versionInvalidator)
    {
        super(
                "Downgrade data schema at version 5 to 4",
                connContext, schemaDetector, schemaDuty, 5, 4,
                null,
                " drop table ts_version_5_done if exists;" +
                " drop index ind_ts_conf_01 if exists;" +
                " drop table ts_version_5 if exists;",
                versionInvalidator);
    }
}
