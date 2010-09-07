package com.enokinomi.timeslice.timeslice;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory
{
    public Connection createConnection(String filename)
    {
        try
        {
            Class.forName("org.hsqldb.jdbcDriver");
            return DriverManager.getConnection("jdbc:hsqldb:file:" + filename + ";shutdown=true;", "SA", "");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Wrapped checked-exception: " + e.getMessage(), e);
        }
    }
}
