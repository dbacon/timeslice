package com.enokinomi.timeslice.lib.ordering.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.impl.BaseHsqldbOps;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public class BaseLowLevelOrderingWorks
{
    private final BaseHsqldbOps baseStore;

    private static interface Table
    {
        public static final String TS_ORDERING = "TS_ORDERING";
    }

    private static interface Field
    {
        public static final String MEMBER = "MEMBER";
        public static final String NAME = "NAME";
        public static final String INDEX = "INDEX";
    }

    @Inject
    BaseLowLevelOrderingWorks(BaseHsqldbOps baseStore)
    {
        this.baseStore = baseStore;
    }

    protected BaseHsqldbOps getBaseStore()
    {
        return baseStore;
    }

    public IConnectionWork<List<String>> workGetSet(final String setName)
    {
        return new IConnectionWork<List<String>>()
        {
            @Override
            public List<String> performWithConnection(Connection conn)
            {
                if (!getBaseStore().versionIsAtLeast(conn, 3)) return null;

                return baseStore.doSomeSql(
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
            }
        };
    }

    public final IConnectionWork<Void> workInsertElementOfSet(final String setName, final int i, final String member)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
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

                return null; // Void
            }
        };
    }

    public final IConnectionWork<Void> workInsertSet(final String setName, final List<String> orderedSetMembers)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                if (getBaseStore().versionIsAtLeast(conn, 3))
                {
                    for (int i = 0; i < orderedSetMembers.size(); ++i)
                    {
                        workInsertElementOfSet(setName, i, orderedSetMembers.get(i)).performWithConnection(conn);
                    }
                }

                return null; // Void
            }
        };
    }

    public final IConnectionWork<Void> workDeleteSetByName(final String setName)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                if (getBaseStore().versionIsAtLeast(conn, 3))
                {
                    baseStore.doSomeSql(
                            conn,
                            "delete from " + Table.TS_ORDERING + " where " + Field.NAME + " = ?",
                            new Object[] { setName },
                            null,
                            null);
                }

                return null; // Void
            }
        };
    }

}
