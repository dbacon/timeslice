package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.google.inject.Inject;

public class SchemaDuty implements ISchemaDuty
{
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
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not create data tables: " + e.getMessage(), e);
        }
    }
}
