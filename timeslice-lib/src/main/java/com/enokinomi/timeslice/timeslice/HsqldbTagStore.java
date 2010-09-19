package com.enokinomi.timeslice.timeslice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.enokinomi.timeslice.app.core.ITagStore;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class HsqldbTagStore implements ITagStore
{
    private static final Timestamp END_OF_TIME = new Timestamp(new DateTime(9999, 12, 31, 0, 0, 0, 0, DateTimeZone.UTC).getMillis());

    private static final String SQL_BilleeLookup = "" +
                        "\n" + " SELECT " +
                        "\n" + "   billee " +
                        "\n" + " FROM " +
                        "\n" + "   ts_assign " +
                        "\n" + " WHERE 1=1 " +
                        "\n" + "   AND eff_from <= ? AND ? < eff_until " +
                        "\n" + "   AND what = ? ";


    private final Connection conn;

    @Inject
    public HsqldbTagStore(@Named("tsConnection") Connection conn)
    {
        this.conn = conn;
    }

    public Connection getConn()
    {
        return conn;
    }

    @Override
    public String lookupBillee(String description, DateTime asOf)
    {
        PreparedStatement statement = null;
        ResultSet rs = null;

        try
        {
            statement = getConn().prepareStatement(SQL_BilleeLookup);

            Timestamp asOfTs = new Timestamp(asOf.getMillis());

            statement.setTimestamp(1, asOfTs);
            statement.setTimestamp(2, asOfTs);
            statement.setString(3, description);

            rs = statement.executeQuery();
            if (!rs.next())
            {
                return "";
            }
            else
            {
                String result = rs.getString(1);

                if (rs.next())
                {
                    throw new RuntimeException("Found >1 row for description.");
                }

                return result;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("looking up billee failed: " + e.getMessage(), e);
        }
        finally
        {
            try
            {
                if (null != statement) statement.close();
            }
            catch (SQLException e)
            {
                throw new RuntimeException("closing statement failed: " + e.getMessage(), e);
            }
            try
            {
                if (null != rs) rs.close();
            }
            catch (SQLException e)
            {
                throw new RuntimeException("closing result-set failed: " + e.getMessage(), e);
            }
        }
    }

    public void assignBillee(String description, String billee, DateTime date)
    {
        endDateAnyBillee(description, date);
        insertBillee(description, billee, date);
    }

    public void endDateAnyBillee(String description, DateTime untilDate)
    {
        // todo: should ensure that no effective records exist for after untilDate

        PreparedStatement statement = null;

        try
        {
            statement = getConn().prepareStatement(
                    " UPDATE ts_assign " +
                    "   SET eff_until = ? " +
                    " WHERE 1=1" +
                    "   AND what = ? " +
                    "   AND eff_until = ? " +
                    "");

            statement.setTimestamp(1, new Timestamp(untilDate.getMillis()));
            statement.setString(2, description);
            statement.setTimestamp(3, END_OF_TIME);
            int rows = statement.executeUpdate();
            statement.close();
            if (rows < 0 || 1 < rows) throw new RuntimeException("assign: 'update' did not result in exactly 1 or 0 rows.");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not update: " + e.getMessage());
        }
        finally
        {
            if (null != statement)
            {
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                    throw new RuntimeException("Closing statement failed: " + e.getMessage(), e);
                }
            }
        }
    }

    protected void insertBillee(String description, String billee, DateTime asOf)
    {
        PreparedStatement statement = null;

        // todo really need check of not closing / clobbering existing range
        // i.e. can now check that there is no effecive record.

        try
        {
            statement = getConn().prepareStatement(
                    " insert into ts_assign (eff_from, eff_until, what, billee) " +
                    " values (?, ?, ?, ?)");

            Timestamp asOfTs = new Timestamp(asOf.getMillis());
            statement.setTimestamp(1, asOfTs);
            statement.setTimestamp(2, END_OF_TIME);
            statement.setString(3, description);
            statement.setString(4, billee);
            int rows = statement.executeUpdate();
            statement.close();
            if (1 != rows) throw new RuntimeException("assign: 'insert' did not result in exactly 1 row.");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not insert: " + e.getMessage());
        }
        finally
        {
            if (null != statement)
            {
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                    throw new RuntimeException("Closing statement failed: " + e.getMessage(), e);
                }
            }
        }
    }
}
