package com.enokinomi.timeslice.web.launcher;


import static com.enokinomi.timeslice.lib.util.Check.mapNullTo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hsqldb.util.SqlTool;

import com.enokinomi.timeslice.branding.api.BrandingAbstractModule;
import com.enokinomi.timeslice.lib.appjob.api.AppJob;
import com.enokinomi.timeslice.lib.appjob.api.AppJobCompletion;
import com.enokinomi.timeslice.lib.appjob.api.IAppJobProcessor;
import com.enokinomi.timeslice.lib.commondatautil.impl.CommonDataModule;
import com.enokinomi.timeslice.lib.userinfo.impl.UserInfoModule;
import com.enokinomi.timeslice.web.appjob.server.impl.AppJobServerModule;
import com.enokinomi.timeslice.web.assign.server.impl.AssignServerModule;
import com.enokinomi.timeslice.web.branding.impl.DefaultBrandingModule;
import com.enokinomi.timeslice.web.guice.GuiceRpcModule;
import com.enokinomi.timeslice.web.login.server.impl.LoginServerModule;
import com.enokinomi.timeslice.web.ordering.server.impl.OrderingServerModule;
import com.enokinomi.timeslice.web.prorata.server.impl.ProRataServerModule;
import com.enokinomi.timeslice.web.session.server.impl.SessionServerModule;
import com.enokinomi.timeslice.web.settings.server.impl.SettingsServerModule;
import com.enokinomi.timeslice.web.task.server.impl.TaskServerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;


public class Driver
{
    private static final Logger log = Logger.getLogger(Driver.class);

    /**
     * Any argument present is taken as the name of settings to be loaded,
     * each having higher precedence than the previous.  System properties
     * are loaded and have the lowest precedence.
     *
     * <p>
     * Properties queried by TsHost are:<ul>
     *   <li><code>timeslice.port</code> - port on which the HTTP listener should listen. </li>
     *   <li><code>timeslice.war</code> - WAR file which the container should expand and deploy/host.</li>
     * </ul>
     * </p>
     *
     * <p>
     * Any property with the name beginning with <code>timeslice.</code> will be passed
     * as init-params to the container context.
     * </p>
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)
    {
        OptionParser parser = new OptionParser();

        ArgumentAcceptingOptionSpec<Integer> portSpec = parser.acceptsAll(Arrays.asList("p", "port"), "Port for web server.").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> dbSpec = parser.acceptsAll(Arrays.asList("d", "data"), "Base-path for database.").withRequiredArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> aclSpec = parser.acceptsAll(Arrays.asList("a", "acl"), "ACL file.").withRequiredArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> resSpec = parser.acceptsAll(Arrays.asList("w", "web-root"), "Base folder of web resources.").withRequiredArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> defResSpec = parser.acceptsAll(Arrays.asList("W", "default-web-root"), "Base folder of web resources.").withRequiredArg().ofType(String.class);
        OptionSpecBuilder listJobsSpec = parser.acceptsAll(Arrays.asList("J", "list-jobs"), "List jobs");
        ArgumentAcceptingOptionSpec<String> runJobSpec = parser.acceptsAll(Arrays.asList("j", "run-job"), "Run job").withRequiredArg().ofType(String.class);
        OptionSpecBuilder debugSpec = parser.acceptsAll(Arrays.asList("D", "debug"), "Set log-level to DEBUG");
        ArgumentAcceptingOptionSpec<String> schemaCreationResourceSpec = parser.acceptsAll(Arrays.asList("S", "schema-creation-resource"), "Use specified resource to create schema if needed").withRequiredArg().ofType(String.class);
        OptionSpecBuilder sqlToolSpec = parser.acceptsAll(Arrays.asList("I", "sql-tool"), "Use interactive SQL tool");

        OptionSet detectedOptions = null;
        try
        {
            detectedOptions = parser.parse(args);
        }
        catch (Exception e)
        {
            System.err.println();
            System.err.println("Bad arguments: " + e.getMessage());
            System.err.println();
            try
            {
                parser.printHelpOn(System.err);
            }
            catch (Exception e1)
            {
                throw new RuntimeException("Could not print option help: " + e.getMessage());
            }
            System.err.println();
            System.exit(1);
        }

        Logger.getRootLogger().setLevel(detectedOptions.has(debugSpec) ? Level.DEBUG : Level.INFO);

        String userHome = System.getProperty("user.home", ".");

        String acl = mapNullTo(aclSpec.value(detectedOptions), userHome + "/.timeslice.acl");
        String db = mapNullTo(dbSpec.value(detectedOptions), userHome + "/.timeslice-data/hsql/default-01");
        final String res = mapNullTo(resSpec.value(detectedOptions), mapNullTo(defResSpec.value(detectedOptions), "webapp"));
        final Integer port = portSpec.value(detectedOptions);

        if (log.isInfoEnabled())
        {
            log.info("config: port     : " + mapNullTo(port, "<not-unspecified>"));
            log.info("config: web-root : " + res);
            log.info("config: ACL      : " + acl);
            log.info("config: data     : " + db);
        }

        Entry<Integer, String> latestVersion = findLatestSchemaVersion();
        log.info("Found latest supported schema version " + latestVersion.getKey());

        String schemaCreationResource =
            detectedOptions.has(schemaCreationResourceSpec)
                ? schemaCreationResourceSpec.value(detectedOptions)
                : latestVersion.getValue();

        log.info("Schema-creation resource: " + schemaCreationResource);

        Injector injector = Guice.createInjector(
                new CommonDataModule(schemaCreationResource, db),
                new UserInfoModule(),
                    new LoginServerModule(),
                    new SettingsServerModule(),
                    new SessionServerModule(acl),
                    new AppJobServerModule(),
                    new ProRataServerModule(),
                    new TaskServerModule(),
                    new AssignServerModule(),
                    new OrderingServerModule(),
                new TsWebLaunchModule(),
                new GuiceRpcModule(),
                figureOutBrandingModule()
        );

        if (detectedOptions.has(listJobsSpec))
        {
            Set<AppJob> jobs = injector.getInstance(Key.get(new TypeLiteral<Set<AppJob>>() {}));
            for (AppJob job: jobs)
            {
                System.out.println(job.getJobId());
            }
        }
        else if (detectedOptions.has(runJobSpec))
        {
            String jobIdToRun = runJobSpec.value(detectedOptions);

            IAppJobProcessor jobProcessor = injector.getInstance(IAppJobProcessor.class);
            AppJobCompletion completion = jobProcessor.performJob(jobIdToRun);

            System.out.printf(
                        "job    : %s\n" +
                        "status : %s\n" +
                        "message: %s\n",
                        completion.getJobId(),
                        completion.getStatus(),
                        completion.getDescription());
        }
        else if (detectedOptions.has(sqlToolSpec))
        {
            try
            {
                SqlTool.objectMain(new String[]
                {
                        "--inlineRc",
                        "url=jdbc:hsqldb:file:" + db + ",user=sa,password=",
                });
            }
            catch (Exception e)
            {
                log.warn("SqlTool raised exception: " + e.getMessage(), e);
            }
        }
        else
        {
            injector.getInstance(TsHost.class).run(port, res);
        }
    }

    private static Entry<Integer, String> findLatestSchemaVersion()
    {
        TreeMap<Integer, String> foundVersions = new TreeMap<Integer, String>();
        for (int i = 0; i < 100; ++i)
        {
            String nextName = "timeslice-" + i + ".ddl";
            if (null != ClassLoader.getSystemClassLoader().getResource(nextName))
            {
                foundVersions.put(i, nextName);
            }
        }
        return foundVersions.lastEntry();
    }

    private static Module figureOutBrandingModule()
    {
        Module brandCompositeModule = new DefaultBrandingModule();

        ServiceLoader<BrandingAbstractModule> stringService = ServiceLoader.load(BrandingAbstractModule.class, ClassLoader.getSystemClassLoader());
        Iterator<BrandingAbstractModule> brandModuleItor = stringService.iterator();
        if (brandModuleItor.hasNext())
        {
            BrandingAbstractModule brandModule = brandModuleItor.next();
            if (log.isInfoEnabled()) log.info("Found branding to use: " + brandModule.getClass().getCanonicalName());
            brandCompositeModule = brandModule;
        }

        if (brandModuleItor.hasNext())
        {
            log.warn("Found more branding modules; only the first module found is applied.");
        }

        return brandCompositeModule;
    }

}
