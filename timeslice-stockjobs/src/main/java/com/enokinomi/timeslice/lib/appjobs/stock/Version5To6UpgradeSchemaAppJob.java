package com.enokinomi.timeslice.lib.appjobs.stock;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.impl.VersionInvalidator;
import com.google.inject.Inject;

public class Version5To6UpgradeSchemaAppJob extends BaseUpgradeSchemaAppJob
{
    @Inject
    Version5To6UpgradeSchemaAppJob(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty, VersionInvalidator versionInvalidator)
    {
        super(
                "Upgrade data schema at version 5 to 6",
                connContext, schemaDetector, schemaDuty, 5, 6,
                null,
                " create table ts_version_6 ( nothing char(1)); " +
                " " +
                " create table ts_user            " +
                " (                               " +
                "     id           identity     not null, " +
                "     active       int          not null, " +
                "     username     varchar(32)  not null, " +
                "     hashscheme   varchar(32)  not null, " +
                "     hashsalt     varchar(255) not null, " +
                "     hashvalue    varchar(255) not null, " +
                "     lastchanged  timestamp    not null " +
                " );                              " +
                " create unique index ind_ts_user_01 on ts_user (username); " +
                " " +
                " " +
                " create table ts_version_6_done ( nothing char(1)); " +
                " ",
                versionInvalidator
                );
    }
}
