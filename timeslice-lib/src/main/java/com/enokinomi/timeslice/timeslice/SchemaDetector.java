package com.enokinomi.timeslice.timeslice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SchemaDetector
{
    public Integer detectSchema(Connection conn)
    {
        boolean foundTagTable = false;
        Integer ddlVersion = Integer.MIN_VALUE;
        List<String> msgs = new ArrayList<String>();

        try
        {
            ResultSet tables = conn.getMetaData().getTables(null, "PUBLIC", "%", null);
            while (tables.next())
            {
                String tableName = tables.getString("TABLE_NAME");
                if ("TS_TAG".equals(tableName))
                {
                    foundTagTable = true;
                }

                if (tableName.startsWith("TS_VERSION_"))
                {
                    String versString = tableName.substring("TS_VERSION_".length());

                    int versEnd = versString.indexOf("_");
                    if (versEnd < 0) versEnd = versString.length();
                    versString = versString.substring(0, versEnd);

                    ddlVersion = Math.max(ddlVersion, Integer.valueOf(versString));
                }
            }
            tables.close();

            if (!foundTagTable) msgs.add("No tag table");
            if (Integer.MIN_VALUE == ddlVersion) msgs.add("No version table");

            // version is at least 0 if tag-table is there.
            if (foundTagTable) ddlVersion = Math.max(0, ddlVersion);
        }
        catch (SQLException e1)
        {
            throw new RuntimeException("Finding existing database failed: " + e1.getMessage(), e1);
        }

        return ddlVersion;
    }
}
