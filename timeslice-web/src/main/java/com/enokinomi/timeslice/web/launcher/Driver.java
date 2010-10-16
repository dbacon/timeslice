package com.enokinomi.timeslice.web.launcher;


import static com.enokinomi.timeslice.lib.util.Check.mapNullTo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceLoader;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.enokinomi.timeslice.lib.commondatautil.CommonDataModule;
import com.enokinomi.timeslice.web.appjob.server.impl.AppJobServerModule;
import com.enokinomi.timeslice.web.assign.server.impl.AssignServerModule;
import com.enokinomi.timeslice.web.branding.api.BrandingAbstractModule;
import com.enokinomi.timeslice.web.branding.impl.DefaultBrandingModule;
import com.enokinomi.timeslice.web.guice.GuiceRpcModule;
import com.enokinomi.timeslice.web.ordering.server.impl.OrderingServerModule;
import com.enokinomi.timeslice.web.prorata.server.impl.ProRataServerModule;
import com.enokinomi.timeslice.web.session.server.impl.SessionModule;
import com.enokinomi.timeslice.web.task.server.impl.TaskServerModule;
import com.google.inject.Guice;
import com.google.inject.Module;


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
        ArgumentAcceptingOptionSpec<Integer> defPortSpec = parser.acceptsAll(Arrays.asList("P", "default-port"), "Port for web server.").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> defResSpec = parser.acceptsAll(Arrays.asList("W", "default-web-root"), "Base folder of web resources.").withRequiredArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> safeDirSpec = parser.acceptsAll(Arrays.asList("s", "safe-dir"), "Safe-dir to save server-side files.").withRequiredArg().ofType(String.class);
        OptionSpecBuilder debugSpec = parser.acceptsAll(Arrays.asList("D", "debug"), "Set log-level to DEBUG");

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
        final Integer port = mapNullTo(portSpec.value(detectedOptions), mapNullTo(defPortSpec.value(detectedOptions), 9080));
        final String safeDir = mapNullTo(safeDirSpec.value(detectedOptions), ".");

        if (log.isInfoEnabled())
        {
            log.info("config: port     : " + port);
            log.info("config: web-root : " + res);
            log.info("config: ACL      : " + acl);
            log.info("config: data     : " + db);
            log.info("config: safedir  : " + safeDir);
        }

        Guice.createInjector(
                new CommonDataModule("timeslice-1.ddl", db),
                    new SessionModule(acl),
                    new AppJobServerModule(),
                    new ProRataServerModule(),
                    new TaskServerModule(safeDir),
                    new AssignServerModule(),
                    new OrderingServerModule(),
                new TsWebLaunchModule(port, res),
                new GuiceRpcModule(),
                figureOutBrandingModule()
            )
            .getInstance(TsHost.class)
            .run();
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
