package com.enokinomi.timeslice.lib.assign;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbStore;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public class HsqldbTagStore implements ITagStore
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

    private static final Logger log = Logger.getLogger(HsqldbTagStore.class);

    private static final Timestamp END_OF_TIME = new Timestamp(new DateTime(9999, 12, 31, 0, 0, 0, 0, DateTimeZone.UTC).getMillis());

    private static final String SQL_BilleeLookup = "" +
                        "\n" + " SELECT " +
                        "\n" + "   billee " +
                        "\n" + " FROM " +
                        "\n" + "   ts_assign " +
                        "\n" + " WHERE 1=1 " +
                        "\n" + "   AND eff_from <= ? AND ? < eff_until " +
                        "\n" + "   AND what = ? ";

    private final BaseHsqldbStore baseStore;

    @Inject
    HsqldbTagStore(BaseHsqldbStore baseStore)
    {
        this.baseStore = baseStore;
    }

    @Override
    public String lookupBillee(String description, DateTime asOf, String valueOnMiss)
    {
        if (baseStore.versionIsAtLeast(1))
        {
            Timestamp asOfTs = new Timestamp(asOf.getMillis());

            List<String> results = baseStore.doSomeSql(
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
                    });

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

    public void assignBillee(String description, String billee, DateTime date)
    {
        if (baseStore.versionIsAtLeast(1))
        {
            endDateAnyBillee(description, date);
            insertBillee(description, billee, date);
        }
    }

    protected void endDateAnyBillee(String description, DateTime untilDate)
    {
        baseStore.require(1);

        // todo: should ensure that no effective records exist for after untilDate

        baseStore.doSomeSql(
                SQL_EndDateOne,
                new Object[]
                {
                        new Timestamp(untilDate.getMillis()),
                        description,
                        END_OF_TIME
                },
                null);
    }

    protected void insertBillee(String description, String billee, DateTime asOf)
    {
        baseStore.require(1);

        // TODO: really need check of not closing / clobbering existing range
        // i.e. can now check that there is no effective record.

        Timestamp asOfTs = new Timestamp(asOf.getMillis());

        baseStore.doSomeSql(
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
    }

    @Override
    public List<String> getAllBillees()
    {
        if (baseStore.versionIsAtLeast(1))
        {
            return baseStore.doSomeSql(
                    SQL_GetBillees,
                    new Object[] {},
                    new ITransformThrowable<ResultSet, String, SQLException>()
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
}
