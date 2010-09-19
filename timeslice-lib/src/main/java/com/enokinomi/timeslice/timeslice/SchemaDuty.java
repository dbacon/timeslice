package com.enokinomi.timeslice.timeslice;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SchemaDuty
{
    private final String schemaResourceName;

    @Inject
    public SchemaDuty(@Named("schemaResource") String schemaResourceName)
    {
        this.schemaResourceName = schemaResourceName;
    }

    public void createSchema(Connection conn)
    {
        try
        {
            InputStream schemaDdlStream = ClassLoader.getSystemResourceAsStream(schemaResourceName);
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
