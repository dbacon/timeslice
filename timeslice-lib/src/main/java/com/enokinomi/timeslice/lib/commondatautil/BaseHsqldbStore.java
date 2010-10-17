package com.enokinomi.timeslice.lib.commondatautil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public final class BaseHsqldbStore
{
    private static final Logger log = Logger.getLogger(BaseHsqldbStore.class);

    private final Connection conn;
    private final SchemaManager schemaManager;

    private Integer version = null;

    @Inject
    public BaseHsqldbStore(Connection conn, SchemaManager schemaManager)
    {
        this.conn = conn;
        this.schemaManager = schemaManager;
    }

    public synchronized boolean versionIsAtLeast(int minversion)
    {
        if (null == version)
        {
            version = schemaManager.findVersion(conn);
        }

        if (log.isDebugEnabled()) log.debug("schema version-check: " + minversion + " <= " + version);

        return minversion <= version;
    }

    public synchronized void require(int minversion)
    {
        if (!versionIsAtLeast(minversion))
        {
            String version2 = Integer.MIN_VALUE == version ? "(unrecognized)" : ("" + version);
            throw new RuntimeException(String.format("Insufficient database version %s, need %s.", version2, minversion));
        }
    }

    public <T> T doSomeSqlSingleResult(String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter)
    {
        List<T> results = doSomeSql(sql, params, rowConverter);

        if (results.size() <= 0) return null;

        if (results.size() == 1) return results.get(0);

        throw new RuntimeException("Expected single result, but " + results.size() + " were returned");
    }

    public <T> List<T> doSomeSql(String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter)
    {
        return doSomeSql(sql, params, rowConverter, null);
    }

    /**
     *
     * @param <T>
     * @param sql
     * @param params
     * @param rowConverter - pass null if statement is update/insert, pass a Transform if it's a query w/ a result-set.
     * @return
     */
    public <T> List<T> doSomeSql(String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter, Integer expectedAffectedRowCount)
    {
        return doSomeSql(conn, sql, params, rowConverter, expectedAffectedRowCount);
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
    public static <T> List<T> doSomeSql(Connection conn, String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter, Integer expectedAffectedRowCount)
    {
        PreparedStatement statement = null;
        ResultSet rs = null;

        try
        {
            statement = conn.prepareStatement(sql);

            int i = 1;
            for (Object param: params)
            {
                if (null == param)
                {
                    statement.setNull(i, Types.NULL);
                    ++i;
                }
                else if (param instanceof SetParam)
                {
                    for (Object setparam: ((SetParam) param).getValues())
                    {
                        if (null == setparam)
                        {
                            statement.setNull(i, Types.NULL);
                        }
                        else
                        {
                            statement.setObject(i, setparam);
                        }
                        ++i;
                    }
                }
                else
                {
                    statement.setObject(i, param);
                    ++i;
                }
            }

            List<T> result = null;

            if (null == rowConverter)
            {
                int rows = statement.executeUpdate();

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
                while (rs.next())
                {
                    result.add(rowConverter.apply(rs));
                }
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
