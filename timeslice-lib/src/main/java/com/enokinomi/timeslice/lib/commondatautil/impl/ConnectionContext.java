package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.google.inject.Inject;

public class ConnectionContext implements IConnectionContext
{
    private final Connection conn;

    @Inject
    public ConnectionContext(Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public <R> R doWorkWithinContext(IConnectionWork<R> work)
    {
        synchronized(conn)
        {
            // initialize work with conn

            try
            {
                return work.performWithConnection(conn);
            }
            catch (RuntimeException e)
            {
                throw e;
            }
            finally
            {
                // clean-up use of conn
            }
        }
    }
}
