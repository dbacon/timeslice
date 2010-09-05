package com.enokinomi.timeslice.timeslice;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Test;


public class HsqldbTimesliceStoreTest
{
    @Test
    public void test1()
    {
        ConnectionFactory connFactory = new ConnectionFactory();
        SchemaDetector schemaDetector = new SchemaDetector();
        HsqldbTimesliceStore store = new HsqldbTimesliceStore("first-task", "target/test-generated-data/abc", 0, new Instant(), new Instant(), connFactory, schemaDetector);
        store.enable(false);
        store.disable();
    }

    @Test
    public void test_billee_1() throws Exception
    {
        String dbDir = "target/test-generated-data/test-1-3-db";

        FileUtils.deleteDirectory(new File(dbDir));

        ConnectionFactory connFactory = new ConnectionFactory();
        SchemaDuty schemaDuty = new SchemaDuty(1, "timeslice-1.ddl");
        schemaDuty.createSchema(connFactory.createConnection(dbDir + "/test-1"));
        SchemaDetector schemaDetector = new SchemaDetector();
        HsqldbTimesliceStore store = new HsqldbTimesliceStore("first-task", dbDir + "/test-1", 1, new Instant(), new Instant(), connFactory, schemaDetector);
        store.enable(false);

        DateTime asOf = new DateTime(2010, 5, 5, 14, 32, 0, 0);
        String billee = store.lookupBillee("desc1", asOf);
        store.disable();

        assertEquals("", billee);

    }

    @Test
    public void test_billee_2() throws Exception
    {
        String dbDir = "target/test-generated-data/test-1-3-db";

        FileUtils.deleteDirectory(new File(dbDir));

        ConnectionFactory connFactory = new ConnectionFactory();
        SchemaDuty schemaDuty = new SchemaDuty(1, "timeslice-1.ddl");
        schemaDuty.createSchema(connFactory.createConnection(dbDir + "/test-2"));
        SchemaDetector schemaDetector = new SchemaDetector();
        HsqldbTimesliceStore store = new HsqldbTimesliceStore("first-task", dbDir + "/test-2", 1, new Instant(), new Instant(), connFactory, schemaDetector);
        store.enable(false);

        String description1 = "desc1";

        DateTime eff1 = new DateTime(2010, 5, 4, 14, 32, 0, 0);
        store.assignBillee(description1, "billee1", eff1);

        DateTime eff2 = new DateTime(2010, 5, 7, 14, 32, 0, 0);
        store.assignBillee(description1, "billee2", eff2);


        DateTime asOfBefore = new DateTime(2010, 5, 3, 14, 32, 0, 0);
        String billeeBefore = store.lookupBillee(description1, asOfBefore);

        DateTime asOfDuring1 = new DateTime(2010, 5, 5, 14, 32, 0, 0);
        String billeeDuring1 = store.lookupBillee(description1, asOfDuring1);

        DateTime asOfDuring2 = new DateTime(2010, 5, 7, 14, 32, 0, 0);
        String billeeDuring2 = store.lookupBillee(description1, asOfDuring2);

        store.disable();

        assertEquals("", billeeBefore);
        assertEquals("billee1", billeeDuring1);
        assertEquals("billee2", billeeDuring2);
    }

}
