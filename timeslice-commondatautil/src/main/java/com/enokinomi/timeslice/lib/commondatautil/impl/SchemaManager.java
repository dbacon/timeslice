package com.enokinomi.timeslice.lib.commondatautil.impl;

import java.sql.Connection;

import com.enokinomi.timeslice.lib.commondatautil.api.IFixedSchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDetector;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaManager;
import com.google.inject.Inject;

public class SchemaManager implements ISchemaManager
{
    private final ISchemaDetector schemaDetector;
    private final IFixedSchemaDuty schemaDuty;

    @Inject
    SchemaManager(ISchemaDetector schemaDetector, IFixedSchemaDuty schemaDuty)
    {
        this.schemaDetector = schemaDetector;
        this.schemaDuty = schemaDuty;
    }

    @Override
    public Integer findVersion(Connection conn)
    {
        Integer version = schemaDetector.detectSchema(conn);

        if (Integer.MIN_VALUE == version)
        {
            schemaDuty.createSchema(conn);

            version = schemaDetector.detectSchema(conn);
        }

        return version;
    }
}
