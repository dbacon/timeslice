package com.enokinomi.timeslice.lib.appjobs.stock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.enokinomi.timeslice.lib.appjob.api.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.util.Mutable;
import com.google.inject.Inject;

public class BrokenUpgradeFix1 implements AppJob
{
    public static final String JobId = "Detect and fix broken upgrade id-1";

    private final IConnectionContext connContext;
    private final ISchemaDetector schemaDetector;
    private final ISchemaDuty schemaDuty;

    @Inject
    BrokenUpgradeFix1(IConnectionContext connContext, ISchemaDetector schemaDetector, ISchemaDuty schemaDuty)
    {
        this.connContext = connContext;
        this.schemaDetector = schemaDetector;
        this.schemaDuty = schemaDuty;
    }

    @Override
    public String getJobId()
    {
        return JobId;
    }

    @Override
    public String perform()
    {
        final Mutable<Exception> preFixDetectException = new Mutable<Exception>(null);
        final Mutable<Exception> postFixDetectException = new Mutable<Exception>(null);
        final Mutable<Boolean> fixed = new Mutable<Boolean>(false);

        String result = connContext.doWorkWithinContext(new IConnectionWork<String>()
        {

            @Override
            public String performWithConnection(Connection conn)
            {
                try
                {
                    schemaDetector.detectSchema(conn);
                }
                catch (Exception e)
                {
                    preFixDetectException.set(e);
                }

                Set<String> tableNames = new LinkedHashSet<String>();
                try
                {
                    ResultSet tables = conn.getMetaData().getTables(null, "PUBLIC", "TS_%", null);
                    while (tables.next())
                    {
                        tableNames.add(tables.getString("TABLE_NAME"));
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Problem reading tables: " + e.getMessage(), e);
                }

                String result;

                if (tableNames.contains("TS_ASSIGN"))
                {
                    boolean hadStarted = false;
                    boolean hadFinished = false;

                    if (!tableNames.contains("TS_VERSION_1"))
                    {
                        schemaDuty.createSchema(conn, "CREATE TABLE TS_VERSION_1 (NOTHING CHAR(1))");
                    }
                    else
                    {
                        hadStarted = true;
                    }

                    if (!tableNames.contains("TS_VERSION_1_DONE"))
                    {
                        schemaDuty.createSchema(conn, "CREATE TABLE TS_VERSION_1_DONE (NOTHING CHAR(1))");
                        if (hadStarted) fixed.set(true);
                    }
                    else
                    {
                        hadFinished = true;
                    }

                    result = String.format("schema is at least version 1 (%sstarted, %sfinished, %sfixed)",
                            hadStarted ? "" : "not ",
                            hadFinished ? "" : "not ",
                            fixed.get() ? "" : "not ");
                }
                else
                {
                    // no TS_ASSIGN found -> schema version is 0
                    result = "schema is earlier than version 1";
                }

                try
                {
                    schemaDetector.detectSchema(conn);
                }
                catch (Exception e)
                {
                    postFixDetectException.set(e);
                }

                return result;
            }
        });

        return result;
    }

}
