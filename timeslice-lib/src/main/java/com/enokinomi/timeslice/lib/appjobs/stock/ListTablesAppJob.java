package com.enokinomi.timeslice.lib.appjobs.stock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.lib.appjob.api.AppJob;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.google.inject.Inject;

public class ListTablesAppJob implements AppJob
{
    private final String jobId = "List tables";
    private final IConnectionContext connContext;

    @Inject
    ListTablesAppJob(IConnectionContext connContext)
    {
        this.connContext = connContext;
    }

    @Override
    public String getJobId()
    {
        return jobId;
    }

    @Override
    public String perform()
    {
        final List<String> tableNames = new ArrayList<String>();

        connContext.doWorkWithinContext(new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                try
                {
                    ResultSet tables = conn.getMetaData().getTables(null, "PUBLIC", "%", null);
                    while (tables.next())
                    {
                        String tableName = tables.getString("TABLE_NAME");
                        if (tableName.startsWith("TS_"))
                        {
                            tableNames.add(tableName);
                        }
                    }
                    tables.close();
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Could not list tables: " + e.getMessage(), e);
                }

                return null; // Void
            }
        });

        return tableNames.toString();
    }

}
