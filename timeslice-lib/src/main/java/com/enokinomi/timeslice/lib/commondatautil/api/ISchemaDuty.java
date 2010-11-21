package com.enokinomi.timeslice.lib.commondatautil.api;

import java.sql.Connection;

public interface ISchemaDuty
{
    void createSchema(Connection conn, String schemaDdl);
}
