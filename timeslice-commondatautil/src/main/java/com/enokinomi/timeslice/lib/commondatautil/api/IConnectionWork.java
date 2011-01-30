package com.enokinomi.timeslice.lib.commondatautil.api;

import java.sql.Connection;

public interface IConnectionWork<R>
{
    public R performWithConnection(Connection conn);
}
