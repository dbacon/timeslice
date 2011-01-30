package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

import org.apache.commons.io.IOUtils;

import com.enokinomi.timeslice.lib.commondatautil.api.IFixedSchemaDuty;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class FixedSchemaDuty implements IFixedSchemaDuty
{
    private final String schemaResourceName;
    private final SchemaDuty schemaDuty;

    @Inject
    FixedSchemaDuty(SchemaDuty schemaDuty, @Named("schemaResource") String schemaResourceName)
    {
        this.schemaDuty = schemaDuty;
        this.schemaResourceName = schemaResourceName;
    }

    @Override
    public void createSchema(Connection conn)
    {
        try
        {
            InputStream schemaDdlStream = ClassLoader.getSystemResourceAsStream(schemaResourceName);

            if (null != schemaDdlStream)
            {
                schemaDuty.createSchema(conn, IOUtils.toString(schemaDdlStream));
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
    }
}
