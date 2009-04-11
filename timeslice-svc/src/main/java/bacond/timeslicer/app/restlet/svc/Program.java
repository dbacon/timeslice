package bacond.timeslicer.app.restlet.svc;

import java.net.URI;

import org.restlet.Component;
import org.restlet.data.Protocol;

import bacond.timeslicer.app.restlet.resource.MyApp;

public class Program
{
	private final int port;
	private final URI rootUri;
	private final String acl;
	private final String safeDir;

	public Program(int port, URI rootUri, String acl, String safeDir)
	{
		this.port = port;
		this.rootUri = rootUri;
		this.acl = acl;
		this.safeDir = safeDir;
	}

	void run(boolean doPreload)
	{
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getClients().add(Protocol.FILE);

		component.getDefaultHost().attach(
				new MyApp(
						component.getContext().createChildContext(),
						rootUri.toString(),
						acl,
						safeDir)
					.preLoadFromFile(doPreload));

		try
		{
			component.start();
		}
		catch (Exception e)
		{
			System.err.println("Caught error, exiting.");
			e.printStackTrace(System.err);
		}
	}
}