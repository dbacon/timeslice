package bacond.timeslicer.restletservice;

import java.net.URI;

import org.restlet.Component;
import org.restlet.data.Protocol;

import bacond.timeslicer.timeslice.TimesliceApp;


public class Program
{
	private final int port;
	private final URI rootUri;
	private final String acl;
	private final String safeDir;
	private final String updateUrl;

	public Program(int port, URI rootUri, String acl, String safeDir, String updateUrl)
	{
		this.port = port;
		this.rootUri = rootUri;
		this.acl = acl;
		this.safeDir = safeDir;
		this.updateUrl = updateUrl;
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
						new TimesliceApp(acl, safeDir, updateUrl)
							.preload(doPreload)));

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