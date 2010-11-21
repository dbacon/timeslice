package com.enokinomi.timeslice.lib.commondatautil;

import java.sql.Connection;

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
    public <R> R doWorkWithinContext(ConnectionWork<R> work)
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
