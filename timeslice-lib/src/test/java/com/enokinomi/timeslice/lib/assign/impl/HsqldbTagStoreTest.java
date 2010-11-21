package com.enokinomi.timeslice.lib.assign.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import com.enokinomi.timeslice.lib.assign.api.ITagStore;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.impl.BaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.impl.ConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.impl.SchemaDuty;
import com.enokinomi.timeslice.lib.util.IoHelp;


public class HsqldbTagStoreTest
{
//    @Test
//    public void test1()
//    {
//        ConnectionFactory connFactory = new ConnectionFactory();
//        HsqldbTimesliceStore store = new HsqldbTimesliceStore(connFactory.createConnection("target/test-generated-data/abc"));
//    }

    @Test
    public void test_billee_1() throws Exception
    {
        String dbDir = "target/test-generated-data/test-1-3-db";

        FileUtils.deleteDirectory(new File(dbDir));

        final int version = 1;

        ConnectionFactory connFactory = new ConnectionFactory();
        ITagStore store = new HsqldbTagStore(
                new HsqldbTagWorks(new BaseHsqldbOps(new MockSchemaManager(version))),
                new ConnectionContext(connFactory.createConnection(dbDir + "/test-1")));

        ISchemaDuty schemaDuty = new SchemaDuty();
        schemaDuty.createSchema(connFactory.createConnection(dbDir + "/test-1"), new IoHelp().readIt(ClassLoader.getSystemResourceAsStream("timeslice-1.ddl")));
//        SchemaDetector schemaDetector = new SchemaDetector();

        DateTime asOf = new DateTime(2010, 5, 5, 14, 32, 0, 0);
        String billee = store.lookupBillee("desc1", asOf, "unassigned");

        assertEquals("unassigned", billee);

    }

    @Test
    public void test_billee_2() throws Exception
    {
        String dbDir = "target/test-generated-data/test-1-3-db";

        FileUtils.deleteDirectory(new File(dbDir));

        final int version = 1;
        ConnectionFactory connFactory = new ConnectionFactory();
        ITagStore store = new HsqldbTagStore(new HsqldbTagWorks(new BaseHsqldbOps(new MockSchemaManager(version))), new ConnectionContext(connFactory.createConnection(dbDir + "/test-1")));

        ISchemaDuty schemaDuty = new SchemaDuty();
        schemaDuty.createSchema(connFactory.createConnection(dbDir + "/test-2"), new IoHelp().readIt(ClassLoader.getSystemResourceAsStream("timeslice-1.ddl")));
//        SchemaDetector schemaDetector = new SchemaDetector();
//        HsqldbTimesliceStore store = new HsqldbTimesliceStore("first-task", dbDir + "/test-2", 1, new Instant(), new Instant(), connFactory, schemaDetector);
//        store.enable(false);

        String description1 = "desc1";

        DateTime eff1 = new DateTime(2010, 5, 4, 14, 32, 0, 0);
        store.assignBillee(description1, "billee1", eff1);

        DateTime eff2 = new DateTime(2010, 5, 7, 14, 32, 0, 0);
        store.assignBillee(description1, "billee2", eff2);


        DateTime asOfBefore = new DateTime(2010, 5, 3, 14, 32, 0, 0);
        String billeeBefore = store.lookupBillee(description1, asOfBefore, "unassigned");

        DateTime asOfDuring1 = new DateTime(2010, 5, 5, 14, 32, 0, 0);
        String billeeDuring1 = store.lookupBillee(description1, asOfDuring1, "unassigned");

        DateTime asOfDuring2 = new DateTime(2010, 5, 7, 14, 32, 0, 0);
        String billeeDuring2 = store.lookupBillee(description1, asOfDuring2, "unassigned");

//        store.disable();

        assertEquals("unassigned", billeeBefore);
        assertEquals("billee1", billeeDuring1);
        assertEquals("billee2", billeeDuring2);
    }

}
