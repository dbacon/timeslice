package bacond.timeslicer.app.svc;

import org.restlet.Component;
import org.restlet.data.Protocol;

import bacond.timeslicer.app.resource.MyApp;

public class Driver
{
	public static void main(String[] args)
	{
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, 8082);
		component.getClients().add(Protocol.FILE);
		component.getDefaultHost().attach(new MyApp(component.getContext().createChildContext()));

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
