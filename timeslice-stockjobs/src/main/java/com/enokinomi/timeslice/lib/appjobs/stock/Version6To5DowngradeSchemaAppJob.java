package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.impl.VersionInvalidator;
import com.google.inject.Inject;

public class Version6To5DowngradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version6To5DowngradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty, VersionInvalidator versionInvalidator)
    {
        super(
                "Downgrade data schema at version 6 to 5",
                connContext, schemaDetector, schemaDuty, 6, 5,
                null,
                " " +
                " drop table ts_version_6_done if exists; " +
                " " +
                " drop index ind_ts_prorata_01 if exists; " +
                " " +
                " drop index ind_ts_user_01 if exists; " +
                " " +
                " drop table ts_user if exists; " +
                " " +
                " drop table ts_version_6 if exists; " +
                " ",
                versionInvalidator);
    }
}
