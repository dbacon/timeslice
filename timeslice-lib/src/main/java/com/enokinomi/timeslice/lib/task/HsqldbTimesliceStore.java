package com.enokinomi.timeslice.lib.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.joda.time.Instant;

import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbStore;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;


public class HsqldbTimesliceStore implements ITimesliceStore
{
    private final BaseHsqldbStore baseStore;

    @Inject
    public HsqldbTimesliceStore(BaseHsqldbStore baseStore)
    {
        this.baseStore = baseStore;
    }

    @Override
    public synchronized void add(StartTag tag)
    {
        baseStore.require(0);

        baseStore.doSomeSql(
                "insert into ts_tag (whenstamp, who, what) values (?, ?, ?)",
                new Object[]
                {
                        tag.getWhen().toString(),
                        tag.getWho(),
                        tag.getWhat(),
                },
                null,
                1);
    }

    @Override
    public synchronized void addAll(Collection<? extends StartTag> tags, boolean strict)
    {
        throw new RuntimeException("TODO: need to implement addAll"); // TODO: addAll()
    }

    @Override
    public synchronized List<StartTag> query(String owner, Instant starting, Instant ending, int pageSize, int pageIndex)
    {
        baseStore.require(0);

        return baseStore.doSomeSql(
                "select limit ? ? whenstamp, who, what from ts_tag where who = ? and whenstamp < ? and whenstamp > ? order by whenstamp desc",
                new Object[]
                {
                        pageIndex*pageSize,
                        pageSize,
                        owner,
                        ending.toString(),
                        starting.toString(),
                },
                new ITransformThrowable<ResultSet, StartTag, SQLException>()
                {
                    @Override
                    public StartTag apply(ResultSet r) throws SQLException
                    {
                        String whenStr = r.getString(1);
                        String who = r.getString(2);
                        String what = r.getString(3);

                        return new StartTag(who, whenStr, what, null);
                    }
                },
                null);
    }

    @Override
    public synchronized void remove(StartTag tag)
    {
        baseStore.require(0);

        baseStore.doSomeSql(
                "delete from ts_tag where whenstamp = ?",
                new Object[] { tag.getWhen().toString() },
                null,
                1);
    }

    @Override
    public synchronized void updateText(StartTag tag)
    {
        baseStore.require(0);

        baseStore.doSomeSql(
                "update ts_tag set what = ? where whenstamp = ?",
                new Object[]
                {
                        tag.getWhat(),
                        tag.getWhen().toString(),
                },
                null,
                1);
    }

}
