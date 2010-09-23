package com.enokinomi.timeslice.web.gwt.server.guice;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.enokinomi.timeslice.branding.BrandingAbstractModule;
import com.enokinomi.timeslice.branding.DefaultBrandingModule;
import com.enokinomi.timeslice.core.SessionModule;
import com.enokinomi.timeslice.core.TimesliceWebModule;
import com.enokinomi.timeslice.lib.appjob_stockjobs.StockJobsModule;
import com.enokinomi.timeslice.lib.assign.AssignModule;
import com.enokinomi.timeslice.lib.task.TaskModule;
import com.enokinomi.timeslice.lib.userinfo.UserInfoModule;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * @see http://stuffthathappens.com/blog/2009/09/14/guice-with-gwt/
 *
 * @author dbacon
 *
 */
@Singleton
public class GuiceRpcService extends RemoteServiceServlet
{
    private static final long serialVersionUID = 1L;

    private Injector injector;

    @Inject
    public GuiceRpcService(Injector injector)
    {
        this.injector = injector;
    }



    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        /*
         * Only when hosted in a servlet container via web.xml (zero-argument constructor, no injector.),
         * the injector will still be null, and we should set it up via context params instead.
         */
        if (null == injector)
        {
            String acl = getServletContext().getInitParameter("timeslice.acl");
            if (null == acl) throw new RuntimeException("No ACL given in context-param 'timeslice.acl'.");

            String db = getServletContext().getInitParameter("timeslice.db");
            if (null == db) throw new RuntimeException("No database base-path given in context-param 'timeslice.db'.");

            String sd = getServletContext().getInitParameter("timeslice.safedir");
            if (null == sd) sd = "."; // throw new RuntimeException("No safe-dir given in context-param 'timeslice.safedir'.");


            Module brandCompositeModule = new DefaultBrandingModule();

            try
            {
                //
                // Note, this doesn't really work when in normal servlet/container mode,
                // the branding module would need to be in the webapp's classpath - system classpath won't be searched.
                //
                ServiceLoader<BrandingAbstractModule> stringService = ServiceLoader.load(BrandingAbstractModule.class, ClassLoader.getSystemClassLoader());
                Iterator<BrandingAbstractModule> brandModuleItor = stringService.iterator();
                if (brandModuleItor.hasNext())
                {
                    BrandingAbstractModule brandModule = brandModuleItor.next();
                    log("overriding brand: " + brandModule.getClass().getCanonicalName());
                    brandCompositeModule = brandModule;
                }

                if (brandModuleItor.hasNext())
                {
                    log("WARNING: Found more branding modules; only the first module found is applied.");
                }
            }
            catch (Exception e)
            {
                log("WARNING: branding failed (will use default): " + e.getMessage());
            }
            catch (ServiceConfigurationError e)
            {
                log("WARNING: branding failed (will use default): " + e.getMessage());
            }

            this.injector = Guice.createInjector(
                    new TaskModule(),
                    new AssignModule(),
                    new UserInfoModule(),
                    new SessionModule(),
                    new GuiceRpcModule(),
                    new StockJobsModule(),
                    new TimesliceWebModule(db, acl, "."),
                    brandCompositeModule);
        }
    }



    /**
     * when hosted in a servlet container via web.xml (zero-argument constructor, no injector.)
     */
    public GuiceRpcService()
    {
        // leave injector null, we'll set it up on init(...)
    }

    @Override
    public String processCall(String payload) throws SerializationException
    {
        RPCRequest req = RPC.decodeRequest(payload, null, this);
        onAfterRequestDeserialized(req);
        return RPC.invokeAndEncodeResponse(
                (RemoteService) injector.getInstance(req.getMethod().getDeclaringClass()),
                req.getMethod(),
                req.getParameters(),
                req.getSerializationPolicy(),
                req.getFlags());
    }

}
