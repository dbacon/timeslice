package bacond.timeslicer.timeslice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;

import bacond.timeslicer.app.core.ITimesliceStore;
import bacond.timeslicer.app.core.StartTag;

public class HsqldbTimesliceStore implements ITimesliceStore
{
    private final Instant starting;
    private final Instant ending;
    private final String firstTagText;

    private final String name;
    private Connection conn = null;
    private final File storeDir;

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

    public void detectSchema(String schemaResourceName)
    {
        ensureLiveConnection();

        boolean found = false;

        try
        {
            ResultSet tables = getConn().getMetaData().getTables(null, "PUBLIC", "%", null);
            while (tables.next())
            {
                if ("TS_TAG".equals(tables.getString("TABLE_NAME")))
                {
                    found = true;
                    break;
                }
            }
            tables.close();
        }
        catch (SQLException e1)
        {
            throw new RuntimeException("Finding existing database failed: " + e1.getMessage(), e1);
        }

        if (!found)
        {
            System.out.println("did not find expected tables.");
            try
            {
                InputStream schemaDdlStream = HsqldbTimesliceStore.class.getClassLoader().getResourceAsStream(schemaResourceName);
                if (null != schemaDdlStream)
                {
                    String schemaDdl = IOUtils.toString(schemaDdlStream);
                    getConn().createStatement().executeUpdate(schemaDdl);
                    System.out.println("created database");
                }
                else
                {
                    throw new RuntimeException("No schema DDL resource '" + schemaResourceName + "' found to load.");
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not load schema resource: " + e.getMessage(), e);
            }
            catch (SQLException e)
            {
                throw new RuntimeException("Could not create data tables: " + e.getMessage(), e);
            }
        }
        else
        {
            System.out.println("Found tables.");
        }
    }

    public HsqldbTimesliceStore(File storeDir, String name, String firstTagText, Instant starting, Instant ending)
    {
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
    public boolean enable()
    {
        try
        {
            Class.forName("org.hsqldb.jdbcDriver");
            setConn(DriverManager.getConnection("jdbc:hsqldb:file:" + generatePath() + ";shutdown=true;", "SA", ""));
            detectSchema("timeslice-0.ddl");
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
}
