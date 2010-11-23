package com.enokinomi.timeslice.lib.prorata.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.impl.BaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.impl.ConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.impl.SchemaDuty;
import com.enokinomi.timeslice.lib.prorata.api.GroupComponent;
import com.enokinomi.timeslice.lib.testing.ConnectionFactory;
import com.enokinomi.timeslice.lib.testing.MockSchemaManager;
import com.enokinomi.timeslice.lib.util.IoHelp;


public class HsqldbStoreTest
{
    private Connection conn;

    @Before
    public void setup() throws Exception
    {
        String dbDir = "target/test-generated-data/test-2-1-db";
        FileUtils.deleteDirectory(new File(dbDir));

        ConnectionFactory connFactory = new ConnectionFactory();
        conn = connFactory.createConnection(dbDir + "/test-1");

        ISchemaDuty sd = new SchemaDuty();
        sd.createSchema(conn, new IoHelp().readSystemResource("timeslice-2.ddl"));
    }

    @After
    public void teardown() throws Exception
    {
        conn.close();
        conn = null;

        String dbDir = "target/test-generated-data/test-2-1-db";
        FileUtils.deleteDirectory(new File(dbDir));
    }

    @Test
    public void test_1() throws Exception
    {
        HsqldbStore store = new HsqldbStore(new ConnectionContext(conn), new ProRataWorks(new BaseHsqldbOps(new MockSchemaManager(2))));

        List<String> groupNames = store.listGroupNames();
        assertNotNull(groupNames);
        assertEquals(0, groupNames.size());
    }

    @Test
    public void test_2() throws Exception
    {
        HsqldbStore store = new HsqldbStore(new ConnectionContext(conn), new ProRataWorks(new BaseHsqldbOps(new MockSchemaManager(2))));

        List<GroupComponent> groupComponents = store.dereferenceGroup("no-group");

        assertNotNull(groupComponents);
        assertEquals(0, groupComponents.size());
    }

    @Test
    public void test_3() throws Exception
    {
        HsqldbStore store = new HsqldbStore(new ConnectionContext(conn), new ProRataWorks(new BaseHsqldbOps(new MockSchemaManager(2))));

        store.addComponent("group1", "project1", BigDecimal.ONE);

        List<GroupComponent> groupComponents = store.dereferenceGroup("group1");

        assertNotNull(groupComponents);
        assertEquals(1, groupComponents.size());
        GroupComponent comp = groupComponents.get(0);
        assertEquals("project1", comp.getName());
        assertEquals(BigDecimal.ONE, comp.getWeight());
    }

    @Test
    public void test_add_delete() throws Exception
    {
        HsqldbStore store = new HsqldbStore(new ConnectionContext(conn), new ProRataWorks(new BaseHsqldbOps(new MockSchemaManager(2))));

        store.addComponent("group1", "project1", BigDecimal.ONE);

        {
            List<GroupComponent> groupComponents = store.dereferenceGroup("group1");

            assertNotNull(groupComponents);
            assertEquals(1, groupComponents.size());
            GroupComponent comp = groupComponents.get(0);
            assertEquals("project1", comp.getName());
            assertEquals(BigDecimal.ONE, comp.getWeight());
        }

        store.removeComponent("group1", "project1");

        List<GroupComponent> groupComponentsPostDelete = store.dereferenceGroup("group1");

        assertNotNull(groupComponentsPostDelete);
        assertEquals(0, groupComponentsPostDelete.size());
    }

}
