package com.enokinomi.timeslice.lib.assign.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.enokinomi.timeslice.lib.assign.api.ITagWorks;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.impl.BaseHsqldbOps;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public class HsqldbTagWorks implements ITagWorks
{
    private static final String SQL_GetBillees = "select distinct billee from ts_assign";

    private static final String SQL_InsertNew = " insert into ts_assign (eff_from, eff_until, what, billee) " +
                        " values (?, ?, ?, ?)";

    private static final String SQL_EndDateOne = " UPDATE ts_assign " +
                        "   SET eff_until = ? " +
                        " WHERE 1=1" +
                        "   AND what = ? " +
                        "   AND eff_until = ? " +
                        "";

    private static final Logger log = Logger.getLogger(HsqldbTagWorks.class);

    private static final Timestamp END_OF_TIME = new Timestamp(new DateTime(9999, 12, 31, 0, 0, 0, 0, DateTimeZone.UTC).getMillis());

    private static final String SQL_BilleeLookup = "" +
                        "\n" + " SELECT " +
                        "\n" + "   billee " +
                        "\n" + " FROM " +
                        "\n" + "   ts_assign " +
                        "\n" + " WHERE 1=1 " +
                        "\n" + "   AND eff_from <= ? AND ? < eff_until " +
                        "\n" + "   AND what = ? ";

    private final BaseHsqldbOps baseStore;

    @Inject
    HsqldbTagWorks(BaseHsqldbOps baseStore)
    {
        this.baseStore = baseStore;
    }

    @Override
    public IConnectionWork<String> workLookupBillee(final String description, final DateTime asOf, final String valueOnMiss)
    {
        return new IConnectionWork<String>()
        {
            @Override
            public String performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, 1))
                {
                    Timestamp asOfTs = new Timestamp(asOf.getMillis());

                    List<String> results = baseStore.doSomeSql(
                            conn,
                            SQL_BilleeLookup,
                            new Object[]
                            {
                                asOfTs,
                                asOfTs,
                                description
                            },
                            new ITransformThrowable<ResultSet, String, SQLException>()
                            {
                                 @Override
                                 public String apply(ResultSet r) throws SQLException
                                 {
                                     return r.getString(1);
                                 }
                            },
                            null);

                    if (results.size() <= 0)
                    {
                        return valueOnMiss;
                    }
                    else if (1 < results.size())
                    {
                        log.warn("Found more than a single result for description '" + description + "', using 1st result. ");
                        return results.get(0);
                    }
                    else
                    {
                        return results.get(0);
                    }
                }
                else
                {
                    return valueOnMiss;
                }
            }
        };
    }

    @Override
    public IConnectionWork<Void> workAssignBillee(final String description, final String billee, final DateTime date)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, 1))
                {
                    workEndDateAnyBillee(description, date).performWithConnection(conn);
                    workInsertBillee(description, billee, date).performWithConnection(conn);
                }

                return null; // Void
            }
        };
    }

    @Override
    public IConnectionWork<Void> workEndDateAnyBillee(final String description, final DateTime untilDate)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                baseStore.require(conn, 1);

                // todo: should ensure that no effective records exist for after untilDate

                baseStore.doSomeSql(
                        conn,
                        SQL_EndDateOne,
                        new Object[]
                        {
                                new Timestamp(untilDate.getMillis()),
                                description,
                                END_OF_TIME
                        },
                        null,
                        null);

                return null;
            }
        };
    }

    @Override
    public IConnectionWork<Void> workInsertBillee(final String description, final String billee, final DateTime asOf)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                baseStore.require(conn, 1);

                // TODO: really need check of not closing / clobbering existing range
                // i.e. can now check that there is no effective record.

                Timestamp asOfTs = new Timestamp(asOf.getMillis());

                baseStore.doSomeSql(
                        conn,
                        SQL_InsertNew,
                        new Object[]
                        {
                                asOfTs,
                                END_OF_TIME,
                                description,
                                billee
                        },
                        null,
                        Integer.valueOf(1));

                return null;
            }
        };
    }

    @Override
    public IConnectionWork<List<String>> workGetAllBillees()
    {
        return new IConnectionWork<List<String>>()
        {
            @Override
            public List<String> performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, 1))
                {
                    return baseStore.doSomeSql(
                            conn,
                            SQL_GetBillees,
                            new Object[] {},
                            new ITransformThrowable<ResultSet, String, SQLException>()
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

}
