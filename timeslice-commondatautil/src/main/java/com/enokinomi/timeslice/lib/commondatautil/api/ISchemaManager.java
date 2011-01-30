package com.enokinomi.timeslice.lib.commondatautil.api;

import java.sql.Connection;

public interface ISchemaManager
{

    Integer findVersion(Connection conn);

}
