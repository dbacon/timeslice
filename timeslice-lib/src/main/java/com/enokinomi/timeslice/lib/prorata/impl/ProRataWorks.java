package com.enokinomi.timeslice.lib.prorata.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.api.IBaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.prorata.api.GroupComponent;
import com.enokinomi.timeslice.lib.prorata.api.IProRataWorks;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.enokinomi.timeslice.lib.util.Pair;
import com.google.inject.Inject;

public class ProRataWorks implements IProRataWorks
{
    private final IBaseHsqldbOps baseStore;

    @Inject
    ProRataWorks(IBaseHsqldbOps baseHsqldbOps)
    {
        this.baseStore = baseHsqldbOps;
    }

    @Override
    public IConnectionWork<Void> workAddComponent(final String groupName, final String componentName, final BigDecimal weight)
    {
        return new IConnectionWork<Void>()
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
    public IConnectionWork<Void> workRemoveComponent(final String groupName, final String componentName)
    {
        return new IConnectionWork<Void>()
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
    public IConnectionWork<List<String>> workListGroupNames()
    {
        return new IConnectionWork<List<String>>()
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
    public IConnectionWork<List<GroupComponent>> workDereferenceGroup(final String groupName)
    {
        return new IConnectionWork<List<GroupComponent>>()
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
    public IConnectionWork<List<Pair<String, List<GroupComponent>>>> workListAllGroupsInfo()
    {
        return new IConnectionWork<List<Pair<String, List<GroupComponent>>>>()
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
