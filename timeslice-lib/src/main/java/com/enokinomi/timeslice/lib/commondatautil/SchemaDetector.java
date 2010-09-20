package com.enokinomi.timeslice.lib.commondatautil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SchemaDetector
{
    public Integer detectSchema(Connection conn)
    {
        boolean foundTagTable = false;
        Integer ddlVersion = Integer.MIN_VALUE;
        List<String> msgs = new ArrayList<String>();
        Map<Integer, String> completionMap = new LinkedHashMap<Integer, String>();

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

                    Integer thisversion = Integer.valueOf(versString);

                    if (tableName.endsWith("_DONE"))
                    {
                        if (completionMap.containsKey(thisversion))
                        {
                            if ("started".equals(completionMap.get(thisversion)))
                            {
                                completionMap.put(thisversion, "complete");
                            }
                            else
                            {
                                throw new RuntimeException("unexpected existing state for completed version " + thisversion);
                            }
                        }
                        else
                        {
                            throw new RuntimeException("Found done w/ no start for version " + thisversion);
                        }
                    }
                    else
                    {
                        if (completionMap.containsKey(thisversion))
                        {
                            throw new RuntimeException("Found 2 starting tables for version " + thisversion);
                        }
                        else
                        {
                            completionMap.put(thisversion, "started");
                        }
                    }
//                    ddlVersion = Math.max(ddlVersion, thisversion);
                }
            }
            tables.close();

            for (Entry<Integer, String> entry: completionMap.entrySet())
            {
                Integer version = entry.getKey();
                String status = entry.getValue();

                if (!"complete".equals(status))
                {
                    throw new RuntimeException("Incomplete upgrade detected at version " + version + ", current status is " + status);
                }
                else
                {
                    ddlVersion = Math.max(ddlVersion, version);
                }
            }

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
