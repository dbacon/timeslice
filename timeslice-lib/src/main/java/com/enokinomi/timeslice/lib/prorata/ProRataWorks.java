package com.enokinomi.timeslice.lib.prorata;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.enokinomi.timeslice.lib.util.Pair;
import com.google.inject.Inject;

public class ProRataWorks implements IProRataWorks
{
    private final BaseHsqldbOps baseStore;

    @Inject
    ProRataWorks(BaseHsqldbOps baseHsqldbOps)
    {
        this.baseStore = baseHsqldbOps;
    }

    @Override
    public ConnectionWork<Void> workAddComponent(final String groupName, final String componentName, final BigDecimal weight)
    {
        return new ConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, 2))
                {
                    baseStore.doSomeSql(conn, "insert into TS_PRORATA (name, component_name, weight) values (?,?,?)", new Object[] { groupName, componentName, weight }, null, null);
                }
                else
                {
                }
                return null; // Void
            }
        };
    }

    @Override
    public ConnectionWork<Void> workRemoveComponent(final String groupName, final String componentName)
    {
        return new ConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, 2))
                {
                    baseStore.doSomeSql(conn, "delete from TS_PRORATA where name = ? and component_name = ?", new Object[] { groupName, componentName }, null, null);
                }
                else
                {
                }

                return null;
            }
        };
    }

    private final class ToGroupComponent implements ITransformThrowable<ResultSet, GroupComponent, SQLException>
    {
        @Override
        public GroupComponent apply(ResultSet r) throws SQLException
        {
            return new GroupComponent(
                    r.getString(1),
                    r.getString(2),
                    new BigDecimal(r.getDouble(3)));
        }
    }

    @Override
    public ConnectionWork<List<String>> workListGroupNames()
    {
        return new ConnectionWork<List<String>>()
        {
            @Override
            public List<String> performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, 2))
                {
                    return baseStore.doSomeSql(conn, "select distinct name from TS_PRORATA", new Object[] { }, new ITransformThrowable<ResultSet, String, SQLException>()
                    {
                        @Override
                        public String apply(ResultSet r) throws SQLException
                        {
                            return r.getString(1);
                        }
                    },
                    null);
                }
                else
                {
                    return Collections.emptyList();
                }
            }
        };
    }

    @Override
    public ConnectionWork<List<GroupComponent>> workDereferenceGroup(final String groupName)
    {
        return new ConnectionWork<List<GroupComponent>>()
        {
            @Override
            public List<GroupComponent> performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, 2))
                {
                    return baseStore.doSomeSql(conn, "select name, component_name,weight from TS_PRORATA where NAME = ?", new Object[] { groupName }, new ToGroupComponent(), null);
                }
                else
                {
                    return Collections.emptyList();
                }
            }
        };
    }

    @Override
    public ConnectionWork<List<Pair<String, List<GroupComponent>>>> workListAllGroupsInfo()
    {
        return new ConnectionWork<List<Pair<String, List<GroupComponent>>>>()
        {
            @Override
            public List<Pair<String, List<GroupComponent>>> performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, 2))
                {
                    List<Pair<String, List<GroupComponent>>> result = new ArrayList<Pair<String, List<GroupComponent>>>();
                    List<String> groupNames = workListGroupNames().performWithConnection(conn);
                    for (String groupName: groupNames)
                    {
                        result.add(Pair.create(groupName, workDereferenceGroup(groupName).performWithConnection(conn)));
                    }
                    return result;
                }
                else
                {
                    return Collections.emptyList();
                }
            }
        };
    }

}
