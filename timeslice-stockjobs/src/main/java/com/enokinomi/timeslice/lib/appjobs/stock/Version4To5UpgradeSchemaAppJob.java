package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.impl.VersionInvalidator;
import com.google.inject.Inject;

public class Version4To5UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version4To5UpgradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty, VersionInvalidator versionInvalidator)
    {
        super(
                "Upgrade data schema at version 4 to 5",
                connContext, schemaDetector, schemaDuty, 4, 5,
                null,
                " create table ts_version_5 ( nothing char(1));" +
                " create unique index ind_ts_conf_01 on ts_conf (username, name, type, value);" +
                " create table ts_version_5_done ( nothing char(1));",
                versionInvalidator);
    }
}
