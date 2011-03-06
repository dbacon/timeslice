package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ConnectionContext implements IConnectionContext
{
    private static final Logger log = Logger.getLogger(ConnectionContext.class);

    private final Provider<Connection> connectionProvider;

    private Connection currentConnection = null;

    @Inject
    ConnectionContext(Provider<Connection> connectionProvider)
    {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public <R> R doWorkWithinWritableContext(IConnectionWork<R> work)
    {
        return doWorkWithinContext(work, false);
    }

    @Override
    public <R> R doWorkWithinReadOnlyContext(IConnectionWork<R> work)
    {
        return doWorkWithinContext(work, false);
    }

    public <R> R doWorkWithinContext(IConnectionWork<R> work, boolean readOnly)
    {
        synchronized(connectionProvider)
        {
            if (currentConnection == null)
            {
                currentConnection = connectionProvider.get();
            }

            try
            {
                log.debug("Transaction beginning.");

                R result = work.performWithConnection(currentConnection);

                if (readOnly)
                {
                    currentConnection.rollback();
                    log.debug("Transaction (r/o) rolled back.");
                }
                else
                {
                    currentConnection.commit();
                    log.debug("Transaction (r/w) committed.");
                }

                return result;
            }
            catch (RuntimeException e)
            {
                try
                {
                    currentConnection.rollback();
                    log.info("Transaction rolled back due to execution error: " + e.getMessage());
                }
                catch (SQLException e1)
                {
                    throw new RuntimeException("Could not roll-back transaction: " + e.getMessage(), e);
                }

                throw new RuntimeException("Error executing transaction: " + e.getMessage(), e);
            }
            catch (SQLException e)
            {
                try
                {
                    currentConnection.rollback();
                    log.info("Transaction rolled back due to commit error: " + e.getMessage());
                }
                catch (SQLException e1)
                {
                    throw new RuntimeException("Could not roll-back transaction: " + e.getMessage(), e);
                }

                throw new RuntimeException("Error commiting transaction: " + e.getMessage(), e);
            }
            finally
            {
            }
        }
    }
}
