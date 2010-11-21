package com.enokinomi.timeslice.lib.commondatautil;

import java.sql.Connection;

public interface ConnectionWork<R>
{
    public R performWithConnection(Connection conn);
}
