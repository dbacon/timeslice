package com.enokinomi.timeslice.timeslice;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

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
