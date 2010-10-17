package com.enokinomi.timeslice.lib.userinfo;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enokinomi.timeslice.lib.assign.ConnectionFactory;
import com.enokinomi.timeslice.lib.assign.MockSchemaManager;
import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbStore;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;


public class UserInfoDaoTest
{
    private Connection conn;

    @Before
    public void setup() throws Exception
    {
        String dbDir = "target/test-generated-data/test-conf-1-db";
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

        String dbDir = "target/test-generated-data/test-conf-1-db";
        FileUtils.deleteDirectory(new File(dbDir));
    }

    private UserInfoDao createStoreUnderTest(int version)
    {
        return new UserInfoDao(new BaseHsqldbStore(conn, new MockSchemaManager(version)));
    }

    @Test
    public void validSettings_unsupportedSchema()
    {
        UserInfoDao dao = createStoreUnderTest(2);

        TsSettings settings = dao.loadUserSettings("junit-1", "");

        assertNotNull("never null settings returned", settings);
    }

    @Test
    public void validSettings_noSettingsForUser()
    {
        UserInfoDao dao = createStoreUnderTest(3);

        TsSettings settings = dao.loadUserSettings("junit-1-no-settings", "");

        assertNotNull("never null settings returned", settings);
    }

    @Test
    public void settings_saveLoadRoundtrip_scalar()
    {
        UserInfoDao dao = createStoreUnderTest(3);

        TsSettings settings = new TsSettings();
        settings.addConfValue("setting1", "custom-set-type", "custom-set-value");

        dao.saveUserSettings("junit-1", settings);

        TsSettings outSettings = dao.loadUserSettings("junit-1", "");

        assertNotNull("never null settings returned", outSettings);
        assertEquals("custom-set-value", outSettings.getScalar("setting1", "default-value"));
    }

    @Test
    public void settings_saveLoadRoundtrip_vector()
    {
        UserInfoDao dao = createStoreUnderTest(3);

        TsSettings settings = new TsSettings();
        settings.addConfValue("list1", "custom-set-type", "custom-set-value-1");
        settings.addConfValue("list1", "custom-set-type", "custom-set-value-2");

        dao.saveUserSettings("junit-1", settings);

        TsSettings outSettings = dao.loadUserSettings("junit-1", "");

        assertNotNull("never null settings returned", outSettings);
        assertEquals(
                Arrays.asList("custom-set-value-1", "custom-set-value-2"),
                outSettings.getListOfStrings("list1", Arrays.asList("default-value")));
    }

}
