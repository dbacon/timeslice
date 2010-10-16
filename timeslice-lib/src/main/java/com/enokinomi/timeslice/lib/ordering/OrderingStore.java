package com.enokinomi.timeslice.lib.ordering;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbStore;
import com.enokinomi.timeslice.lib.commondatautil.SchemaManager;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public class OrderingStore extends BaseHsqldbStore implements IOrderingStore<String>
{
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
    public OrderingStore(Connection conn, SchemaManager schemaManager)
    {
        super(conn, schemaManager);
    }

    @Override
    public List<String> requestOrdering(String setName, List<String> unorderedSetValues)
    {
        if (versionIsAtLeast(3))
        {
            List<String> ordering = doSomeSql(
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
                    });

                return new OrderApplier().<String>applyOrdering(unorderedSetValues, ordering);
        }
        else
        {
            return unorderedSetValues;
        }
    }

    @Override
    public void setOrdering(String setName, List<String> orderedSetMembers)
    {
        if (versionIsAtLeast(3))
        {
            doSomeSql(
                    "delete from " + Table.TS_ORDERING + " where " + Field.NAME + " = ?",
                    new Object[] { setName },
                    null);

            for (int i = 0; i < orderedSetMembers.size(); ++i)
            {
                String member = orderedSetMembers.get(i);
                doSomeSql(
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
                    null);
            }
        }
    }
}
