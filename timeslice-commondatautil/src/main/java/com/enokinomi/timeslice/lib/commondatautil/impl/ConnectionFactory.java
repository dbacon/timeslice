package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory
{
    private final String filename;

    public ConnectionFactory(String filename)
    {
        this.filename = filename;
    }

    public Connection createConnection()
    {
        try
        {
            Class.forName("org.hsqldb.jdbcDriver");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:" + filename + ";shutdown=true;", "SA", "");
//            conn.setAutoCommit(false);
            return conn;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Wrapped checked-exception: " + e.getMessage(), e);
        }
    }
}
