package com.enokinomi.timeslice.timeslice;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class SchemaDuty
{
    private final Integer thisVersion;
    private final String schemaResourceName;

    public SchemaDuty(Integer thisVersion, String schemaResourceName)
    {
        this.thisVersion = thisVersion;
        this.schemaResourceName = schemaResourceName;
    }

    public Integer getThisVersion()
    {
        return thisVersion;
    }

    public Integer detectSchema(Connection conn)
    {
        boolean foundTagTable = false;
        Integer ddlVersion = Integer.MIN_VALUE;
        List<String> msgs = new ArrayList<String>();

        try
        {
            ResultSet tables = conn.getMetaData().getTables(null, "PUBLIC", "%", null);
            while (tables.next())
            {
                String tableName = tables.getString("TABLE_NAME");
                if ("TS_TAG".equals(tableName))
                {
                    foundTagTable = true;
                }

                if (tableName.startsWith("TS_VERSION_"))
                {
                    String versString = tableName.substring("TS_VERSION_".length());

                    int versEnd = versString.indexOf("_");
                    if (versEnd < 0) versEnd = versString.length();
                    versString = versString.substring(0, versEnd);

                    ddlVersion = Math.max(ddlVersion, Integer.valueOf(versString));
                }
            }
            tables.close();

            if (!foundTagTable) msgs.add("No tag table");
            if (Integer.MIN_VALUE == ddlVersion) msgs.add("No version table");

            if (foundTagTable) ddlVersion = Math.max(0, ddlVersion);
        }
        catch (SQLException e1)
        {
            throw new RuntimeException("Finding existing database failed: " + e1.getMessage(), e1);
        }

        return ddlVersion;
    }

    public void ensureSchema(Connection conn, boolean allowMigration)
    {
        Integer ddlVersion = detectSchema(conn);

        if (ddlVersion >= 0)
        {
            if (ddlVersion == thisVersion)
            {
                System.out.println("Found expected DDL version " + thisVersion + ".");
            }
            else
            {
                if (allowMigration)
                {
                    autoMigrate(ddlVersion, thisVersion);
                }
                else
                {
                    throw new RuntimeException("Wrong database version found.");
                }
            }
        }
        else
        {
            System.out.println("Could not recognize an existing schema, attempting to create.");
            createSchema(conn);
        }
    }

    protected void autoMigrate(Integer ddlVersion, Integer thisVersion)
    {
        unsupportedMigration(ddlVersion, thisVersion);
    }

    protected final void unsupportedMigration(Integer ddlVersion, Integer thisVersion)
    {
        throw new RuntimeException("Unsupported automatic migration from version " + ddlVersion + " to " + thisVersion + ".");
    }

    public void createSchema(Connection conn)
    {
        try
        {
            InputStream schemaDdlStream = HsqldbTimesliceStore.class.getClassLoader().getResourceAsStream(schemaResourceName);
            if (null != schemaDdlStream)
            {
                String schemaDdl = IOUtils.toString(schemaDdlStream);
                conn.createStatement().executeUpdate(schemaDdl);
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


}
