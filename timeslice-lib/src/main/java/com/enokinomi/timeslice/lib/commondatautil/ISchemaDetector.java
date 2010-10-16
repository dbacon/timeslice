package com.enokinomi.timeslice.lib.commondatautil;

import java.sql.Connection;

public interface ISchemaDetector
{

    Integer detectSchema(Connection conn);

}
