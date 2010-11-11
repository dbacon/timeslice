package com.enokinomi.timeslice.lib.ordering2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbStore;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;

public class BaseLowLevelOrderingStore
{
    private final BaseHsqldbStore baseStore;

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

    BaseLowLevelOrderingStore(BaseHsqldbStore baseStore)
    {
        this.baseStore = baseStore;
    }

    protected BaseHsqldbStore getBaseStore()
    {
        return baseStore;
    }

    public List<String> getSet(String setName)
    {
        return baseStore.doSomeSql(
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
    }

    public void insertSet(String setName, List<String> orderedSetMembers)
    {
        for (int i = 0; i < orderedSetMembers.size(); ++i)
        {
            insertElementOfSet(setName, i, orderedSetMembers.get(i));
        }
    }

    public void insertElementOfSet(String setName, int i, String member)
    {
        baseStore.doSomeSql(
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

    public void deleteSetByName(String setName)
    {
        baseStore.doSomeSql(
                "delete from " + Table.TS_ORDERING + " where " + Field.NAME + " = ?",
                new Object[] { setName },
                null);
    }

}
