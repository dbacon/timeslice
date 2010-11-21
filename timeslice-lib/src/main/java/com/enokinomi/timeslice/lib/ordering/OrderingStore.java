package com.enokinomi.timeslice.lib.ordering;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.IConnectionContext;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public class OrderingStore implements IOrderingStore<String>
{
    private final BaseHsqldbOps baseStore;
    private final IConnectionContext connContext;

    public static interface Table
    {
        public static final String TS_ORDERING = "TS_ORDERING";
    }

    public static interface Field
    {
        public static final String MEMBER = "MEMBER";
        public static final String NAME = "NAME";
        public static final String INDEX = "INDEX";
    }

    @Inject
    public OrderingStore(BaseHsqldbOps baseStore, IConnectionContext connContext)
    {
        this.baseStore = baseStore;
        this.connContext = connContext;
    }

    @Override
    public List<String> requestOrdering(final String setName, final List<String> unorderedSetValues)
    {
        return connContext.doWorkWithinContext(workRequestOrdering(setName, unorderedSetValues));
    }

    private ConnectionWork<List<String>> workRequestOrdering(final String setName,
            final List<String> unorderedSetValues)
    {
        return new ConnectionWork<List<String>>()
        {
            @Override
            public List<String> performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, 3))
                {
                    List<String> ordering = baseStore.doSomeSql(
                            conn,
                            "select " + Field.MEMBER +
                            " from " + Table.TS_ORDERING +
                            " where " + Field.NAME + " = ?" +
                            " order by " + Field.INDEX + " ASC",
                            new Object[] { setName },
                            new ITransformThrowable<ResultSet, String, SQLException>()
                            {
                                @Override
                                public String apply(ResultSet r) throws SQLException
                                {
                                    return r.getString(Field.MEMBER);
                                }
                            },
                            null);

                        return new OrderApplier().<String>applyOrdering(unorderedSetValues, ordering);
                }
                else
                {
                    return unorderedSetValues;
                }
            }
        };
    }

    @Override
    public void setOrdering(final String setName, final List<String> orderedSetMembers)
    {
        connContext.doWorkWithinContext(new ConnectionWork<List<String>>()
        {
            @Override
            public List<String> performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, 3))
                {
                    baseStore.doSomeSql(
                            conn,
                            "delete from " + Table.TS_ORDERING + " where " + Field.NAME + " = ?",
                            new Object[] { setName },
                            null,
                            null);

                    for (int i = 0; i < orderedSetMembers.size(); ++i)
                    {
                        String member = orderedSetMembers.get(i);
                        baseStore.doSomeSql(
                            conn,
                            "insert into " + Table.TS_ORDERING + " (" +
                                    Field.NAME + "," +
                                    Field.MEMBER + "," +
                                    Field.INDEX +
                                    ") values (?, ?, ?)",
                            new Object[]
                            {
                                setName,
                                member,
                                i
                            },
                            null,
                            null);
                    }
                }

                return null; // Void
            }
        });
    }
}
