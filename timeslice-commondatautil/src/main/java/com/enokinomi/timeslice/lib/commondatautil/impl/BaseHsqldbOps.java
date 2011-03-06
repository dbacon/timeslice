package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.enokinomi.timeslice.lib.commondatautil.api.IBaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaManager;
import com.enokinomi.timeslice.lib.commondatautil.api.SetParam;
import com.enokinomi.timeslice.lib.commondatautil.impl.VersionInvalidator.Invalidator;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public class BaseHsqldbOps implements IBaseHsqldbOps
{
    private static final Logger log = Logger.getLogger(BaseHsqldbOps.class);

    private final ISchemaManager schemaManager;

    private Integer version = null;

    @Inject
    BaseHsqldbOps(ISchemaManager schemaManager, VersionInvalidator versionInvalidator)
    {
        this.schemaManager = schemaManager;

        versionInvalidator.register(new Invalidator()
        {
            @Override
            public void invalidate()
            {
                version = null;
            }
        });
    }

    @Override
    public synchronized boolean versionIsAtLeast(Connection conn, int minversion)
    {
        if (null == version)
        {
            version = schemaManager.findVersion(conn);
        }

        if (log.isDebugEnabled()) log.debug("schema version-check: " + minversion + " <= " + version);

        return minversion <= version;
    }

    @Override
    public synchronized void require(Connection conn, int minversion)
    {
        if (!versionIsAtLeast(conn, minversion))
        {
            String version2 = Integer.MIN_VALUE == version ? "(unrecognized)" : ("" + version);
            throw new RuntimeException(String.format("Insufficient database version %s, need %s.", version2, minversion));
        }
    }

    @Override
    public <T> T doSomeSqlSingleResult(Connection conn, String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter)
    {
        List<T> results = doSomeSql(conn, sql, params, rowConverter, null);

        if (results.size() <= 0) return null;

        if (results.size() == 1) return results.get(0);

        throw new RuntimeException("Expected single result, but " + results.size() + " were returned");
    }

    /**
     *
     * @param <T>
     * @param conn
     * @param sql
     * @param params
     * @param rowConverter - pass null if statement is update/insert, pass a Transform if it's a query w/ a result-set.
     * @return
     */
    @Override
    public <T> List<T> doSomeSql(Connection conn, String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter, Integer expectedAffectedRowCount)
    {
        PreparedStatement statement = null;
        ResultSet rs = null;

        try
        {
            log.debug("statement: " + sql);
            statement = conn.prepareStatement(sql);

            int i = 1;
            for (Object param: params)
            {
                if (null == param)
                {
                    log.debug("  param: NULL");
                    statement.setNull(i, Types.NULL);
                    ++i;
                }
                else if (param instanceof SetParam)
                {
                    log.debug("  set-param:");
                    for (Object setparam: ((SetParam) param).getValues())
                    {
                        if (null == setparam)
                        {
                            log.debug("    NULL");
                            statement.setNull(i, Types.NULL);
                        }
                        else
                        {
                            log.debug("    " + setparam.toString());
                            statement.setObject(i, setparam);
                        }
                        ++i;
                    }
                }
                else
                {
                    log.debug("  " + param.toString());
                    statement.setObject(i, param);
                    ++i;
                }
            }

            List<T> result = null;

            if (null == rowConverter)
            {
                int rows = statement.executeUpdate();

                log.debug("rows affected: " + rows);

                if (null != expectedAffectedRowCount)
                {
                    if (rows != expectedAffectedRowCount)
                    {
                        throw new RuntimeException("Expected " + expectedAffectedRowCount + " affected rows, but found " + rows);
                    }
                }
            }
            else
            {
                rs = statement.executeQuery();

                result = new ArrayList<T>();
                int readCount = 0;
                while (rs.next())
                {
                    ++readCount;
                    result.add(rowConverter.apply(rs));
                }

                log.debug("read and converted: " + readCount);
            }

            return result;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("SQL failed: " + e.getMessage(), e);
        }
        finally
        {
            try
            {
                if (null != statement) statement.close();
            }
            catch (SQLException e)
            {
                throw new RuntimeException("closing statement failed: " + e.getMessage(), e);
            }
            try
            {
                if (null != rs) rs.close();
            }
            catch (SQLException e)
            {
                throw new RuntimeException("closing result-set failed: " + e.getMessage(), e);
            }
        }
    }
}
