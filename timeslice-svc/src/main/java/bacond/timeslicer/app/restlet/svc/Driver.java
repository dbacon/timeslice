package bacond.timeslicer.app.restlet.svc;

import org.restlet.Component;
import org.restlet.data.Protocol;

import bacond.timeslicer.app.restlet.resource.MyApp;

public class Driver
{
	public static final String DefaultLocalRoot = "file:///C:/Documents and Settings/dbacon/Desktop/java/eclipse-workspaces-SR1/ws-0/timeslice-web/target/timeslice-web-1.0.0-SNAPSHOT";

	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.exit(-1);
		}
		
		String localRootUri = args[0];
		
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, 8082);
		component.getClients().add(Protocol.FILE);
		component.getDefaultHost()
			.attach(new MyApp(component.getContext().createChildContext(), localRootUri));

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
