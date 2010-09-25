package com.enokinomi.timeslice.lib.prorata;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.enokinomi.timeslice.lib.commondatautil.SchemaManager;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public class HsqldbStore implements IProRataStore
{
    private static final Logger log = Logger.getLogger(HsqldbStore.class);

    private final Connection conn;
    private final SchemaManager schemaManager;

    private Integer version = null;

    @Inject
    public HsqldbStore(Connection conn, SchemaManager schemaManager)
    {
        this.conn = conn;
        this.schemaManager = schemaManager;
    }

    private synchronized boolean versionIsAtLeast(int minversion)
    {
        if (null == version)
        {
            version = schemaManager.findVersion(conn);
        }

        if (log.isDebugEnabled()) log.debug("schema version-check: " + minversion + " <= " + version);

        return minversion <= version;
    }

    @Override
    public void addComponent(String groupName, String componentName, BigDecimal weight)
    {
        if (versionIsAtLeast(2))
        {
            doSomeSql("insert into TS_PRORATA (name, component_name, weight) values (?,?,?)", new Object[] { groupName, componentName, weight }, null);
        }
        else
        {
        }
    }

    @Override
    public void removeComponent(String groupName, String componentName)
    {
        if (versionIsAtLeast(2))
        {
            doSomeSql("delete from TS_PRORATA where name = ? and component_name = ?", new Object[] { groupName, componentName }, null);
        }
        else
        {
        }
    }

    public static final class SetParam
    {
        private final Object[] values;

        public SetParam(Object[] values)
        {
            this.values = values;
        }

        public Object[] getValues()
        {
            return values;
        }
    }

    protected <T> List<T> doSomeSql(String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter)
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
                }

                ++i;
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

    @Override
    public List<String> listGroupNames()
    {
        if (versionIsAtLeast(2))
        {
            return doSomeSql("select distinct name from TS_PRORATA", new Object[] { }, new ITransformThrowable<ResultSet, String, SQLException>()
            {
                @Override
                public String apply(ResultSet r) throws SQLException
                {
                    return r.getString(1);
                }
            });
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @Override
    public List<GroupComponent> dereferenceGroup(String groupName)
    {
        if (versionIsAtLeast(2))
        {
            return doSomeSql("select component_name,weight from TS_PRORATA where NAME = ?", new Object[] { groupName }, new ITransformThrowable<ResultSet, GroupComponent, SQLException>()
                    {
                        @Override
                        public GroupComponent apply(ResultSet r) throws SQLException
                        {
                            return new GroupComponent(
                                    r.getString(1),
                                    new BigDecimal(r.getDouble(2)));
                        }
                    });
        }
        else
        {
            return Collections.emptyList();
        }
    }

}
