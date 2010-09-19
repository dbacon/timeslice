package com.enokinomi.timeslice.launcher;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.gwt.server.rpc.AppJob;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ListTablesAppJob implements AppJob
{
    private final String jobId = "list-tables";
    private final Connection conn;

    @Inject
    public ListTablesAppJob(@Named("tsConnection") Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public String getJobId()
    {
        return jobId;
    }

    @Override
    public String perform()
    {
        List<String> tableNames = new ArrayList<String>();

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

        return tableNames.toString();
    }

}
