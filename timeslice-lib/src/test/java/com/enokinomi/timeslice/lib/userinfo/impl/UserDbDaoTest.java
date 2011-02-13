package com.enokinomi.timeslice.lib.userinfo.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enokinomi.timeslice.lib.commondatautil.api.IBaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaDuty;
import com.enokinomi.timeslice.lib.commondatautil.impl.CommonDataFactory;
import com.enokinomi.timeslice.lib.commondatautil.impl.ConnectionFactory;
import com.enokinomi.timeslice.lib.testing.MockSchemaManager;
import com.enokinomi.timeslice.lib.util.IoHelp;


public class UserDbDaoTest
{
    private Connection conn;

    private static final String dbDir = "target/test-generated-data/test-account-1-db";
    private static final int schemaVer = 6;

    @Before
    public void setup() throws Exception
    {
        FileUtils.deleteDirectory(new File(dbDir));

        ConnectionFactory connFactory = new ConnectionFactory(dbDir + "/test-1");
        conn = connFactory.createConnection();

        ISchemaDuty sd = new CommonDataFactory().createSchemaDuty();
        sd.createSchema(conn, new IoHelp().readSystemResource("timeslice-" + schemaVer + ".ddl"));
    }

    @After
    public void teardown() throws Exception
    {
        conn.close();
        conn = null;

        FileUtils.deleteDirectory(new File(dbDir));
    }

    private UserInfoDao createStoreUnderTest(int version)
    {
        CommonDataFactory f = new CommonDataFactory();
        IBaseHsqldbOps ops = f.createBaseHsqldbOps(new MockSchemaManager(version));
        return new UserInfoDao(f.createJoiningConnectionContext(conn), new UserInfoWorks(ops), new UserDbWorks(ops));
    }

    @Test
    public void validSettings_unsupportedSchema()
    {
        UserInfoDao dao = createStoreUnderTest(schemaVer-1);
        int count = dao.userCount();

        assertEquals(0, count);

        boolean authentic = dao.authenticate("user-1", "xyz");

        assertFalse(authentic);

    }

    @Test
    public void validSettings_function()
    {
        UserInfoDao dao = createStoreUnderTest(schemaVer);

        assertEquals(0, dao.userCount());
        assertFalse(dao.authenticate("user-1", "xyz"));

        dao.createUser("user-0", "xyz", Sha1V1Scheme.class.getCanonicalName());

        assertEquals("user created", 1, dao.userCount());
        assertTrue("correct password accepts", dao.authenticate("user-0", "xyz"));
        assertFalse("wrong password denies  ", dao.authenticate("user-0", "xyq"));
    }

}
