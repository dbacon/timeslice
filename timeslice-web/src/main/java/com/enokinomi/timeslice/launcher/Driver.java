package com.enokinomi.timeslice.launcher;


import static com.enokinomi.timeslice.lib.util.Check.mapNullTo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceLoader;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.enokinomi.timeslice.web.gwt.server.task.GuiceRpcService;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;


public class Driver
{
    private final Integer port;
    private final String res;

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

        String userHome = System.getProperty("user.home", ".");

        String acl = mapNullTo(aclSpec.value(detectedOptions), userHome + "/.timeslice.acl");
        String db = mapNullTo(dbSpec.value(detectedOptions), userHome + "/.timeslice-data/hsql/default-01");
        final String res = mapNullTo(resSpec.value(detectedOptions), mapNullTo(defResSpec.value(detectedOptions), "webapp"));
        final Integer port = mapNullTo(portSpec.value(detectedOptions), mapNullTo(defPortSpec.value(detectedOptions), 9080));

        System.out.println("Configuration:");
        System.out.println("  port     : " + port);
        System.out.println("  web-root : " + res);
        System.out.println("  ACL      : " + acl);
        System.out.println("  data     : " + db);
        System.out.flush();


        Module brandCompositeModule = new DefaultBrandingModule();

        ServiceLoader<BrandingAbstractModule> stringService = ServiceLoader.load(BrandingAbstractModule.class, ClassLoader.getSystemClassLoader());
        Iterator<BrandingAbstractModule> brandModuleItor = stringService.iterator();
        if (brandModuleItor.hasNext())
        {
            BrandingAbstractModule brandModule = brandModuleItor.next();
            System.out.println("Found branding to use: " + brandModule.getClass().getCanonicalName());
            brandCompositeModule = brandModule;
        }

        if (brandModuleItor.hasNext())
        {
            System.out.println("Found more branding modules; only the first module found is applied.");
        }

        Guice.createInjector(
                new ServletModule()
                {
                    @Override
                    protected void configureServlets()
                    {
                        serve("/timeslice.App/gwtrpc").with(GuiceRpcService.class);
                    }
                },
                new TimesliceModule(db, acl),
                new AbstractModule()
                {
                    @Override
                    protected void configure()
                    {
                    }

                    @SuppressWarnings("unused")
                    @Provides Driver createDriver()
                    {
                        return new Driver(port, res);
                    }
                },
                brandCompositeModule
            )
            .getInstance(Driver.class)
            .run();
    }

    public Driver(Integer port, String res)
    {
        this.port = port;
        this.res = res;
    }

    public void run()
    {
        new TsHost(port)
            .createGuiceContext("/", res)
            .run();
    }
}
