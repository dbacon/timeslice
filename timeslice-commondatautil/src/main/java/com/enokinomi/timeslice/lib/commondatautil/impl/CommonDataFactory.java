package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.api.IBaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaManager;
import com.google.inject.Provider;

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

    public IConnectionContext createJoiningConnectionContext(final Connection conn)
    {
        return new IConnectionContext()
        {
            @Override
            public <R> R doWorkWithinWritableContext(IConnectionWork<R> work)
            {
                return work.performWithConnection(conn);
            }

            @Override
            public <R> R doWorkWithinReadOnlyContext(IConnectionWork<R> work)
            {
                return work.performWithConnection(conn);
            }
        };
    }

    public IConnectionContext createConnectionContext(final ConnectionFactory connFactory)
    {
        return new ConnectionContext(new Provider<Connection>()
                {
                    @Override
                    public Connection get()
                    {
                        return connFactory.createConnection();
                    }
                });
    }

    public IBaseHsqldbOps createBaseHsqldbOps(ISchemaManager schemaManager)
    {
        return new BaseHsqldbOps(schemaManager, new VersionInvalidator());
    }
}
