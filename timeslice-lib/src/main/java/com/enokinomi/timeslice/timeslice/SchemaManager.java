package com.enokinomi.timeslice.timeslice;

import java.sql.Connection;

import com.google.inject.Inject;

public class SchemaManager
{
    private final SchemaDetector schemaDetector;
    private final SchemaDuty schemaDuty;

    @Inject
    public SchemaManager(SchemaDetector schemaDetector, SchemaDuty schemaDuty)
    {
        this.schemaDetector = schemaDetector;
        this.schemaDuty = schemaDuty;
    }

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
