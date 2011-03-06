package com.enokinomi.timeslice.lib.task.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.Instant;

import com.enokinomi.timeslice.lib.commondatautil.api.IBaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.task.api.ITimesliceWorks;
import com.enokinomi.timeslice.lib.task.api.StartTag;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public class TimesliceWorks implements ITimesliceWorks
{
    private final IBaseHsqldbOps baseHsqldbOps;

    @Inject
    TimesliceWorks(IBaseHsqldbOps baseStore)
    {
        this.baseHsqldbOps = baseStore;
    }

    @Override
    public IConnectionWork<Void> workAdd(final StartTag tag)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                baseHsqldbOps.require(conn, 0);

                baseHsqldbOps.doSomeSql(
                        conn,
                        "insert into ts_tag (whenstamp, who, what) values (?, ?, ?)",
                        new Object[]
                        {
                                tag.getWhen().toString(),
                                tag.getWho(),
                                tag.getWhat(),
                        },
                        null,
                        1);

                return null; // Void
            }
        };
    }
    @Override
    public IConnectionWork<List<StartTag>> workQuery(final String owner, final Instant starting, final Instant ending, final int pageSize, final int pageIndex)
    {
        return new IConnectionWork<List<StartTag>>()
        {
            @Override
            public List<StartTag> performWithConnection(Connection conn)
            {
                baseHsqldbOps.require(conn, 0);

                return baseHsqldbOps.doSomeSql(
                        conn,
                        "select limit ? ? whenstamp, who, what from ts_tag where who = ? and ? < whenstamp and whenstamp < ? order by whenstamp desc",
                        new Object[]
                        {
                                pageIndex*pageSize,
                                pageSize,
                                owner,
                                starting.toString(),
                                ending.toString(),
                        },
                        new ITransformThrowable<ResultSet, StartTag, SQLException>()
                        {
                            @Override
                            public StartTag apply(ResultSet r) throws SQLException
                            {
                                String whenStr = r.getString(1);
                                String who = r.getString(2);
                                String what = r.getString(3);

                                return new StartTag(who, whenStr, what, null, false);
                            }
                        },
                        null);
            }
        };
    }

    @Override
    public IConnectionWork<Void> workRemove(final StartTag tag)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                baseHsqldbOps.require(conn, 0);

                baseHsqldbOps.doSomeSql(
                        conn,
                        "delete from ts_tag where who = ? and whenstamp = ?",
                        new Object[] { tag.getWho(), tag.getWhen().toString() },
                        null,
                        1);

                return null; // Void
            }
        };
    }

    @Override
    public IConnectionWork<Void> workUpdateText(final StartTag tag)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                baseHsqldbOps.require(conn, 0);

                baseHsqldbOps.doSomeSql(
                        conn,
                        "update ts_tag set what = ? where whenstamp = ?",
                        new Object[]
                        {
                                tag.getWhat(),
                                tag.getWhen().toString(),
                        },
                        null,
                        1);

                return null; // Void
            }
        };
    }

}
