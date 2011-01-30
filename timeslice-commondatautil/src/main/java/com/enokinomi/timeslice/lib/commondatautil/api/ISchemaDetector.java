package com.enokinomi.timeslice.lib.commondatautil.api;

import java.sql.Connection;

public interface ISchemaDetector
{

    Integer detectSchema(Connection conn);

}
