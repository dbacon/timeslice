package com.enokinomi.timeslice.lib.prorata;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbStore;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.enokinomi.timeslice.lib.util.Pair;
import com.google.inject.Inject;

public class HsqldbStore implements IProRataStore
{
    private final BaseHsqldbStore baseStore;

    @Inject
    public HsqldbStore(BaseHsqldbStore baseStore)
    {
        this.baseStore = baseStore;
    }

    @Override
    public void addComponent(String groupName, String componentName, BigDecimal weight)
    {
        if (baseStore.versionIsAtLeast(2))
        {
            baseStore.doSomeSql("insert into TS_PRORATA (name, component_name, weight) values (?,?,?)", new Object[] { groupName, componentName, weight }, null);
        }
        else
        {
        }
    }

    @Override
    public void removeComponent(String groupName, String componentName)
    {
        if (baseStore.versionIsAtLeast(2))
        {
            baseStore.doSomeSql("delete from TS_PRORATA where name = ? and component_name = ?", new Object[] { groupName, componentName }, null);
        }
        else
        {
        }
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
    public List<String> listGroupNames()
    {
        if (baseStore.versionIsAtLeast(2))
        {
            return baseStore.doSomeSql("select distinct name from TS_PRORATA", new Object[] { }, new ITransformThrowable<ResultSet, String, SQLException>()
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
        if (baseStore.versionIsAtLeast(2))
        {
            return baseStore.doSomeSql("select name, component_name,weight from TS_PRORATA where NAME = ?", new Object[] { groupName }, new ToGroupComponent());
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Pair<String, List<GroupComponent>>> listAllGroupsInfo()
    {
        if (baseStore.versionIsAtLeast(2))
        {
            List<Pair<String, List<GroupComponent>>> result = new ArrayList<Pair<String, List<GroupComponent>>>();
            List<String> groupNames = listGroupNames();
            for (String groupName: groupNames)
            {
                result.add(Pair.create(groupName, dereferenceGroup(groupName)));
            }
            return result;
        }
        else
        {
            return Collections.emptyList();
        }
    }

}
