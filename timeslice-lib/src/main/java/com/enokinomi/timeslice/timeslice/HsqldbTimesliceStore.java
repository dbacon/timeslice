package com.enokinomi.timeslice.timeslice;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import com.enokinomi.timeslice.app.core.ITimesliceStore;
import com.enokinomi.timeslice.app.core.StartTag;


public class HsqldbTimesliceStore implements ITimesliceStore
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

    private final Instant starting;
    private final Instant ending;
    private final String firstTagText;

    private final String name;
    private Connection conn = null;
    private final File storeDir;
    private final SchemaDuty schemaDuty;

    protected void ensureLiveConnection()
    {
        if (null == getConn()) throw new RuntimeException("Not enabled.");
        try
        {
//            if (!getConn().isValid(250))
//            {
//                throw new RuntimeException("Enabled, but connection not valid.");
//            }
            if (getConn().isClosed())
            {
                throw new RuntimeException("Enabled, but connection is closed.");
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Enabled, but connection check failed: " + e.getMessage());
        }
    }

    public File getStoreDir()
    {
        return storeDir;
    }

    public SchemaDuty getSchemaDuty()
    {
        return schemaDuty;
    }

    Connection getConn()
    {
        return conn;
    }

    void setConn(Connection conn)
    {
        this.conn = conn;
    }

    public String getName()
    {
        return name;
    }

    public HsqldbTimesliceStore(SchemaDuty schemaDuty, File storeDir, String name, String firstTagText, Instant starting, Instant ending)
    {
        this.schemaDuty = schemaDuty;
        this.storeDir = storeDir;
        this.name = name;
        this.firstTagText = firstTagText;
        this.starting = starting;
        this.ending = ending;
    }

    @Override
    public boolean disable()
    {
        try
        {
            conn.close();

            setConn(null);
            return true;
        }
        catch (SQLException e)
        {
            return false;
        }
    }

    private String generatePath()
    {
        File db = new File(getName());
        if (!db.isAbsolute())
        {
            db = new File(getStoreDir(), getName());
        }

        return db.toString();
    }

    @Override
    public boolean enable(boolean allowMigration)
    {
        try
        {
            Class.forName("org.hsqldb.jdbcDriver");
            setConn(DriverManager.getConnection("jdbc:hsqldb:file:" + generatePath() + ";shutdown=true;", "SA", ""));
            schemaDuty.ensureSchema(getConn(), allowMigration);

            return true;
        }
        catch (SQLException e)
        {
            return false;
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("Could not load HSQLDB JDBC driver.");
            return false;
        }
    }

    @Override
    public boolean isEnabled()
    {
        return null != getConn();
    }


    @Override
    public String getFirstTagText()
    {
        return firstTagText;
    }

    @Override
    public Instant getStarting()
    {
        return starting;
    }

    @Override
    public Instant getEnding()
    {
        return ending;
    }

    //TODO enforce starting/ending ? do we need this ?

    @Override
    public void add(StartTag tag)
    {
        ensureLiveConnection();

        try
        {
            PreparedStatement statement = getConn().prepareStatement("insert into ts_tag (whenstamp, who, what) values (?, ?, ?)");
            statement.setString(1, tag.getWhen().toString());
            statement.setString(2, tag.getWho());
            statement.setString(3, tag.getWhat());
            int rows = statement.executeUpdate();
            statement.close();
            if (1 != rows) throw new RuntimeException("add: 'insert' did not result in exactly 1 row.");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not insert: " + e.getMessage());
        }
    }

    @Override
    public void addAll(Collection<? extends StartTag> tags, boolean strict)
    {
        ensureLiveConnection();
        throw new RuntimeException("TODO: need to implement addAll"); // TODO: addAll()
    }

    @Override
    public List<StartTag> query(String owner, Instant starting, Instant ending, int pageSize, int pageIndex)
    {
        ensureLiveConnection();

        // TODO: use page-size and page-index
        try
        {
            ArrayList<StartTag> result = new ArrayList<StartTag>(100);
            PreparedStatement statement = getConn().prepareStatement("select limit ? ? whenstamp, who, what from ts_tag where who = ? and whenstamp < ? and whenstamp > ? order by whenstamp desc");
            statement.setInt(1, pageIndex*pageSize);
            statement.setInt(2, pageSize);
            statement.setString(3, owner);
            statement.setString(4, ending.toString());
            statement.setString(5, starting.toString());
            ResultSet rs = statement.executeQuery();
            while (rs.next())
            {
                String whenStr = rs.getString(1);
                String who = rs.getString(2);
                String what = rs.getString(3);

                result.add(new StartTag(who, whenStr, what, null));
            }

            statement.close();

            return result;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("query failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void remove(StartTag tag)
    {
        ensureLiveConnection();

        try
        {
            PreparedStatement statement = getConn().prepareStatement("delete from ts_tag where whenstamp = ?");
            statement.setString(1, tag.getWhen().toString());
            int deleted = statement.executeUpdate();
            if (1 != deleted) throw new RuntimeException("delete affected " + deleted + " rows instead of a single row.");
            statement.close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("deleted failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateText(StartTag tag)
    {
        ensureLiveConnection();

        try
        {
            PreparedStatement statement = getConn().prepareStatement("update ts_tag set what = ? where whenstamp = ?");
            statement.setString(1, tag.getWhat());
            statement.setString(2, tag.getWhen().toString());
            int affected = statement.executeUpdate();
            if (1 != affected) throw new RuntimeException("update-text affected " + affected + " rows instead of a single row.");
            statement.close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("update-text failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String lookupBillee(String description, DateTime asOf)
    {
        ensureLiveConnection();

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
        if (schemaDuty.getThisVersion() < 1) throw new RuntimeException("Feature requires newer database.");

        endDateAnyBillee(description, date);
        insertBillee(description, billee, date);
    }

    public void endDateAnyBillee(String description, DateTime untilDate)
    {
        ensureLiveConnection();

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
        ensureLiveConnection();

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
