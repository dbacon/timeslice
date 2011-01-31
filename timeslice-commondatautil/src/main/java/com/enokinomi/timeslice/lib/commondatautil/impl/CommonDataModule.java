package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.enokinomi.timeslice.lib.commondatautil.api.IBaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IFixedSchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaManager;
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
        bind(ISchemaDuty.class).to(SchemaDuty.class);
        bind(ISchemaManager.class).to(SchemaManager.class);

        bind(IFixedSchemaDuty.class).to(FixedSchemaDuty.class);
        bind(String.class).annotatedWith(Names.named("schemaResource")).toInstance(schemaResourceName);

        bind(IBaseHsqldbOps.class).to(BaseHsqldbOps.class);

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
            st.execute("SET WRITE_DELAY 0 MILLIS");
            st.close();
            log.debug("Set write-delay to 0ms");

            log.debug(String.format("Created connection: %s (%d)", conn.toString(), conn.hashCode()));
            return conn;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Wrapped checked-exception: " + e.getMessage(), e);
        }
    }

}
