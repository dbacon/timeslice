package com.enokinomi.timeslice.lib.commondatautil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class CommonDataModule extends AbstractModule
{
    private static final Logger log = Logger.getLogger(CommonDataModule.class);

    private final String schemaResourceName;

    private final String dbFilename;

    public CommonDataModule(String schemaResourceName, String dbFilename)
    {
        this.schemaResourceName = schemaResourceName;
        this.dbFilename = dbFilename;
    }

    @Override
    protected void configure()
    {
        bind(String.class).annotatedWith(Names.named("schemaResource")).toInstance(schemaResourceName);

        ConnectionContext connContext = new ConnectionContext(createNew());
        bind(IConnectionContext.class).toInstance(connContext);

        bind(ISchemaDetector.class).to(SchemaDetector.class);
    }

    private Connection createNew()
    {
        try
        {
            Class.forName("org.hsqldb.jdbcDriver");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:" + dbFilename + ";shutdown=true;", "SA", "");

            Statement st = conn.createStatement();
            st.execute("SET WRITE_DELAY 500 MILLIS");
            st.close();
            log.debug("Set write-delay to 500ms");

            log.debug(String.format("Created connection: %s (%d)", conn.toString(), conn.hashCode()));
            return conn;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Wrapped checked-exception: " + e.getMessage(), e);
        }
    }

}
