package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.google.inject.Inject;

public class SchemaDuty implements ISchemaDuty
{
    private static final Logger log = Logger.getLogger(SchemaDuty.class);

    @Inject
    SchemaDuty()
    {
    }

    @Override
    public void createSchema(Connection conn, String schemaDdl)
    {
        try
        {
            conn.createStatement().executeUpdate(schemaDdl);
            log.debug("created database");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not create data tables: " + e.getMessage(), e);
        }
    }
}
