package com.enokinomi.timeslice.lib.assign;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.SchemaManager;

public final class MockSchemaManager extends SchemaManager
{
    private final int version;

    public MockSchemaManager(int version)
    {
        super(null, null);

        this.version = version;
    }

    @Override
    public Integer findVersion(Connection conn)
    {
        return version;
    }
}
