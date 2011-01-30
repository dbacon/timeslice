package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.api.IBaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaManager;

/** For getting a hold of useful objects without pulling in and using Guice */
public class CommonDataFactory
{
    public CommonDataFactory()
    {
    }

    public ISchemaDuty createSchemaDuty()
    {
        return new SchemaDuty();
    }

    public IConnectionContext createConnectionContext(Connection conn)
    {
        return new ConnectionContext(conn);
    }

    public IBaseHsqldbOps createBaseHsqldbOps(ISchemaManager schemaManager)
    {
        return new BaseHsqldbOps(schemaManager);
    }
}
