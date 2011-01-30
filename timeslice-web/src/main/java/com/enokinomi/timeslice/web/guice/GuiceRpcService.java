package com.enokinomi.timeslice.web.guice;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * @see http://stuffthathappens.com/blog/2009/09/14/guice-with-gwt/
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
