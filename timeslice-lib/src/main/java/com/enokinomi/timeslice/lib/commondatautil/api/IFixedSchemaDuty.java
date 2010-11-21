package com.enokinomi.timeslice.lib.commondatautil.api;

import java.sql.Connection;

public interface IFixedSchemaDuty
{

    void createSchema(Connection conn);

}
