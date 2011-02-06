package com.enokinomi.timeslice.web.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.enokinomi.timeslice.lib.appjob.api.AppJobCompletion;
import com.enokinomi.timeslice.lib.appjob.api.IAppJobProcessor;
import com.enokinomi.timeslice.lib.appjobs.stock.BrokenUpgradeFix1;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.commondatautil.api.ISchemaManager;
import com.enokinomi.timeslice.lib.commondatautil.impl.CommonDataModule;
import com.enokinomi.timeslice.web.appjob.server.impl.AppJobServerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Issue149_DataSchemaUpgradeBadStateTest
{
    private static final String ListTables = "List tables";
    private static final String ShowSchemaVersion = "Show schema version";
    private static final String Upgrade01 = "Upgrade data schema at version 0 to 1";
    private static final String Upgrade12 = "Upgrade data schema at version 1 to 2";
    private static final String Upgrade23 = "Upgrade data schema at version 2 to 3";
    private static final String Upgrade34 = "Upgrade data schema at version 3 to 4";
    private static final String Upgrade45 = "Upgrade data schema at version 4 to 5";

    private static final String[] jobs = {
            ListTables,
            ShowSchemaVersion,
            BrokenUpgradeFix1.JobId,
            Upgrade01,
            Upgrade12,
            Upgrade23,
            Upgrade34,
            Upgrade45,
    };

    String[] version3Tables = {
            "TS_ASSIGN",
            "TS_ORDERING",
            "TS_PRORATA",
            "TS_TAG",
            "TS_VERSION_3",
            "TS_VERSION_3_DONE",
    };

    String[] version4Tables = {
            "TS_TAG",
            "TS_ASSIGN",
            "TS_PRORATA",
            "TS_ORDERING",
            "TS_CONF",
            "TS_VERSION_4",
            "TS_VERSION_4_DONE",
    };

    @Test
    public void test_issue_149()
    {
        FileUtils.deleteQuietly(new File("target/test-db/db-149"));

        Injector injector = Guice.createInjector(
                new CommonDataModule("timeslice-3.ddl", "target/test-dbs/iss-149/db"),
                new AppJobServerModule()
                );

        final ISchemaManager schemaManager = injector.getInstance(ISchemaManager.class);

        IConnectionContext connContext = injector.getInstance(IConnectionContext.class);

        Integer version = connContext.doWorkWithinContext(new IConnectionWork<Integer>()
        {
            @Override
            public Integer performWithConnection(Connection conn)
            {
                return schemaManager.findVersion(conn);
            }
        });

        assertEquals(Integer.valueOf(3), version);

        IAppJobProcessor appJobProcessor = injector.getInstance(IAppJobProcessor.class);

        List<String> availableJobIds = appJobProcessor.getAvailableJobIds();
        // this test only works by calling out known jobs - may need to be tweaked.
        assertEquals(Arrays.asList(jobs), availableJobIds);

        AppJobCompletion result;

        assertTableList(appJobProcessor, version3Tables);
        assertVersionDetectionOkAt(appJobProcessor, 3);

        result = appJobProcessor.performJob(Upgrade01);
        assertEquals("failed", result.getStatus());
        assertTableList(appJobProcessor, version3Tables);
        assertVersionDetectionOkAt(appJobProcessor, 3);

        result = appJobProcessor.performJob(Upgrade12);
        assertEquals("failed", result.getStatus());
        assertTableList(appJobProcessor, version3Tables);
        assertVersionDetectionOkAt(appJobProcessor, 3);

        result = appJobProcessor.performJob(Upgrade23);
        assertEquals("failed", result.getStatus());
        assertTableList(appJobProcessor, version3Tables);
        assertVersionDetectionOkAt(appJobProcessor, 3);

        result = appJobProcessor.performJob(Upgrade34);
        assertEquals("ok", result.getStatus());
        assertVersionDetectionOkAt(appJobProcessor, 4);

        result = appJobProcessor.performJob(Upgrade01);
        assertEquals("failed", result.getStatus());
        assertTableList(appJobProcessor, version4Tables);
        assertVersionDetectionOkAt(appJobProcessor, 4);

        result = appJobProcessor.performJob(Upgrade12);
        assertEquals("failed", result.getStatus());
        assertTableList(appJobProcessor, version4Tables);
        assertVersionDetectionOkAt(appJobProcessor, 4);

        result = appJobProcessor.performJob(Upgrade23);
        assertEquals("failed", result.getStatus());
        assertTableList(appJobProcessor, version4Tables);
        assertVersionDetectionOkAt(appJobProcessor, 4);

        result = appJobProcessor.performJob(Upgrade34);
        assertEquals("failed", result.getStatus());
        assertTableList(appJobProcessor, version4Tables);
        assertVersionDetectionOkAt(appJobProcessor, 4);

        // TODO: make assertions about db - that it can correctly determine
        //       its version/upgrade state.
    }

    private void assertTableList(IAppJobProcessor appJobProcessor, String ... tables)
    {
        Set<String> tableSet = new LinkedHashSet<String>(Arrays.asList(tables));

        AppJobCompletion result = appJobProcessor.performJob(ListTables);
        assertEquals("ok", result.getStatus());
        String tableList = result.getDescription();
        assertTrue(tableList.startsWith("["));
        assertTrue(tableList.endsWith("]"));
        String[] tablesFound = tableList.substring(1, tableList.length() - 1).split(",");
        Set<String> foundTables = new LinkedHashSet<String>();
        for (String tableFound: tablesFound)
        {
            foundTables.add(tableFound.trim());
        }

        assertTrue(tableSet.containsAll(tableSet));
    }

    private void assertVersionDetectionOkAt(IAppJobProcessor appJobProcessor, int version)
    {
        AppJobCompletion result;
        result = appJobProcessor.performJob(ShowSchemaVersion);
        assertEquals("ok", result.getStatus());
        assertEquals(
                "Data schema version detection: " +
                "detected version " + version,
                result.getDescription());
    }
}
