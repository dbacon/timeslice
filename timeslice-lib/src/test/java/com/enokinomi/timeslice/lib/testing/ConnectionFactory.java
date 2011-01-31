package com.enokinomi.timeslice.lib.testing;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory
{
    public Connection createConnection(String filename)
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