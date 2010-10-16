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

public class BaseHsqldbStore
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

    protected synchronized boolean versionIsAtLeast(int minversion)
    {
        if (null == version)
        {
            version = schemaManager.findVersion(conn);
        }

        if (log.isDebugEnabled()) log.debug("schema version-check: " + minversion + " <= " + version);

        return minversion <= version;
    }

    /**
     *
     * @param <T>
     * @param sql
     * @param params
     * @param rowConverter - pass null if statement is update/insert, pass a Transform if it's a query w/ a result-set.
     * @return
     */
    protected <T> List<T> doSomeSql(String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter)
    {
        return doSomeSql(conn, sql, params, rowConverter);
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
    protected static <T> List<T> doSomeSql(Connection conn, String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter)
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
                statement.executeUpdate();
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
