package bacond.timeslicer.restletservice;

import java.net.URI;

import org.restlet.Component;
import org.restlet.data.Protocol;

import bacond.timeslicer.timeslice.TimesliceApp;


public class Program
{
	private final int port;
	private final URI rootUri;
	private final TimesliceApp timesliceApp;

	public Program(int port, URI rootUri, TimesliceApp timesliceApp)
	{
		this.port = port;
		this.rootUri = rootUri;
		this.timesliceApp = timesliceApp;
	}

	void run()
	{
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getClients().add(Protocol.FILE);

		component.getDefaultHost().attach(
				new MyApp(
						component.getContext().createChildContext(),
						rootUri.toString(),
						timesliceApp));

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
