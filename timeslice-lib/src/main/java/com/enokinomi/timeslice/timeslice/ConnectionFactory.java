package com.enokinomi.timeslice.timeslice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory
{
    public Connection createConnection(String filename) throws ClassNotFoundException, SQLException
    {
        Class.forName("org.hsqldb.jdbcDriver");
        return DriverManager.getConnection("jdbc:hsqldb:file:" + filename + ";shutdown=true;", "SA", "");
    }
}
