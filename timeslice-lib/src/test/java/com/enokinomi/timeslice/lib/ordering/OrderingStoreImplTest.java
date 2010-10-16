package com.enokinomi.timeslice.lib.ordering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enokinomi.timeslice.lib.assign.ConnectionFactory;
import com.enokinomi.timeslice.lib.assign.MockSchemaManager;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;


public class OrderingStoreImplTest
{
    String red = "red";
    String orange = "orange";
    String yellow = "yellow";
    String green = "green";
    String blue = "blue";
    String nonmember = "nonmember";

    private Connection conn;

    @Before
    public void setup() throws Exception
    {
        String dbDir = "target/test-generated-data/test-ordering-1-db";
        FileUtils.deleteDirectory(new File(dbDir));

        ConnectionFactory connFactory = new ConnectionFactory();
        conn = connFactory.createConnection(dbDir + "/test-1");

        SchemaDuty sd = new SchemaDuty("timeslice-3.ddl");
        sd.createSchema(conn);
    }

    @After
    public void teardown() throws Exception
    {
        conn.close();
        conn = null;

        String dbDir = "target/test-generated-data/test-ordering-1-db";
        FileUtils.deleteDirectory(new File(dbDir));
    }

    private IOrderingStore<String> createStoreUnderTest()
    {
        return new OrderingStore(conn, new MockSchemaManager(3));
//        return new MemoryOrderingStore<String>();
    }

    @Test
    public void test1_setOrdering()
    {
        IOrderingStore<String> os = createStoreUnderTest();

        List<String> set1 = Arrays.asList(red, orange, yellow);
        os.setOrdering("s1", set1);
    }

    @Test
    public void test1_requestOnUnknownSet()
    {
        IOrderingStore<String> os = createStoreUnderTest();

        List<String> set1 = Arrays.asList(red, orange, yellow);
        List<String> orderedSet1 = os.requestOrdering("s1", set1);

        assertEquals(3, orderedSet1.size());
        assertEquals(set1, orderedSet1);
    }

    @Test
    public void test1_requestOnKnownSet_full()
    {
        IOrderingStore<String> os = createStoreUnderTest();

        List<String> set1 = Arrays.asList(red, orange, yellow);
        os.setOrdering("s1", set1);

        List<String> orderedSet1 = os.requestOrdering("s1", set1);

        assertEquals(3, orderedSet1.size());
        assertEquals(set1, orderedSet1);
        assertFalse(Arrays.asList(red, yellow, orange).equals(orderedSet1));
    }

    @Test
    public void test1_requestOnKnownSet_subset()
    {
        IOrderingStore<String> os = createStoreUnderTest();

        List<String> set1 = Arrays.asList(red, orange, yellow, green, blue);
        os.setOrdering("s1", set1);

        List<String> orderedSet1 = os.requestOrdering("s1", Arrays.asList(green, red, blue));

        assertEquals(3, orderedSet1.size());
        assertEquals(Arrays.asList(red, green, blue), orderedSet1);
    }

    @Test
    public void test1_requestOnKnownSet_superset()
    {
        IOrderingStore<String> os = createStoreUnderTest();

        List<String> set1 = Arrays.asList(red, orange, yellow, green, blue);
        os.setOrdering("s1", set1);

        List<String> orderedSet1 = os.requestOrdering("s1", Arrays.asList(green, red, blue, nonmember, orange, yellow));

        assertEquals(6, orderedSet1.size());
        assertEquals(Arrays.asList(red, orange, yellow, green, blue, nonmember), orderedSet1);
    }

    @Test
    public void test1_requestOnKnownSet_intersect()
    {
        IOrderingStore<String> os = createStoreUnderTest();

        List<String> set1 = Arrays.asList(red, orange, yellow, green, blue);
        os.setOrdering("s1", set1);

        List<String> orderedSet1 = os.requestOrdering("s1", Arrays.asList(green, red, nonmember, blue, orange));

        assertEquals(5, orderedSet1.size());
        assertEquals(Arrays.asList(red, orange, green, blue, nonmember), orderedSet1);
    }

    @Test
    public void test1_requestOnUnknownSet_othersKnown()
    {
        IOrderingStore<String> os = createStoreUnderTest();

        List<String> set1 = Arrays.asList(red, orange, yellow, green, blue);
        os.setOrdering("s1", set1);

        List<String> orderedSet1 = os.requestOrdering("s2", Arrays.asList(green, red, nonmember, blue, orange));

        assertEquals(5, orderedSet1.size());
        assertEquals(Arrays.asList(green, red, nonmember, blue, orange), orderedSet1);
    }

}
