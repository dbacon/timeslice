package com.enokinomi.timeslice.timeslice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        SchemaDuty schemaDuty = new SchemaDuty(0, "timeslice-0.ddl");
        HsqldbTimesliceStore store = new HsqldbTimesliceStore(schemaDuty, new File("."), "target/test-generated-data/abc", "first-task", new Instant(), new Instant());
        store.enable(false);
        store.disable();
    }

    @Test(expected=RuntimeException.class)
    public void test_upgrade_0_to_1() throws Exception
    {
        FileUtils.deleteDirectory(new File("target/test-generated-data/test-1-1-db"));

        SchemaDuty schemaDuty = new SchemaDuty(1, "timeslice-0.ddl");
        HsqldbTimesliceStore store = new HsqldbTimesliceStore(schemaDuty, new File("."), "target/test-generated-data/test-1-1-db/test-1", "first-task", new Instant(), new Instant());
        store.enable(false);
        store.disable();

        schemaDuty = new SchemaDuty(1, "timeslice-1.ddl");
        store = new HsqldbTimesliceStore(schemaDuty, new File("."), "target/test-generated-data/test-1-1-db/test-1", "first-task", new Instant(), new Instant());
        store.enable(false);
        fail("should be unreached");
    }

    @Test(expected=RuntimeException.class)
    public void test_upgrade_1_to_0() throws Exception
    {
        String dbDir = "target/test-generated-data/test-1-2-db";

        FileUtils.deleteDirectory(new File(dbDir));

        SchemaDuty schemaDuty = new SchemaDuty(1, "timeslice-1.ddl");
        HsqldbTimesliceStore store = new HsqldbTimesliceStore(schemaDuty, new File("."), dbDir + "/test-1", "first-task", new Instant(), new Instant());
        store.enable(false);
        store.disable();

        schemaDuty = new SchemaDuty(0, "timeslice-0.ddl");
        store = new HsqldbTimesliceStore(schemaDuty, new File("."), dbDir + "/test-1", "first-task", new Instant(), new Instant());
        store.enable(false);
        fail("should be unreached");
    }

    @Test
    public void test_billee_1() throws Exception
    {
        String dbDir = "target/test-generated-data/test-1-3-db";

        FileUtils.deleteDirectory(new File(dbDir));

        SchemaDuty schemaDuty = new SchemaDuty(1, "timeslice-1.ddl");
        HsqldbTimesliceStore store = new HsqldbTimesliceStore(schemaDuty, new File("."), dbDir + "/test-1", "first-task", new Instant(), new Instant());
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

        SchemaDuty schemaDuty = new SchemaDuty(1, "timeslice-1.ddl");
        HsqldbTimesliceStore store = new HsqldbTimesliceStore(schemaDuty, new File("."), dbDir + "/test-1", "first-task", new Instant(), new Instant());
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
