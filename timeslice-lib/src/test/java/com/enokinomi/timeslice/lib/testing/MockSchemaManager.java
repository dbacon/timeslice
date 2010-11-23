package com.enokinomi.timeslice.lib.testing;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaManager;

public final class MockSchemaManager implements ISchemaManager
{
    private final int version;

    public MockSchemaManager(int version)
    {
        this.version = version;
    }

    @Override
    public Integer findVersion(Connection conn)
    {
        return version;
    }
}
