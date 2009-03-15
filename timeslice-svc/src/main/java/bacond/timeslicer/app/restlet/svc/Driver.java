package bacond.timeslicer.app.restlet.svc;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.restlet.Component;
import org.restlet.data.Protocol;

import bacond.timeslicer.app.restlet.resource.MyApp;

public class Driver
{
	public static class Args
	{
		public static final String Root = "root";
		public static final String Port = "port";
		public static final String Acl = "acl";
	}
	
	private static CommandLine hello(String[] args) throws ParseException
	{
		Options opts = new Options();
		opts.addOption(null, Args.Port, true, "Listening port.");
		opts.addOption(null, Args.Root, true, "Path of shared folder.");
		opts.addOption(null, Args.Acl, true, "ACL filename.");
		
		return new GnuParser().parse(opts, args);
	}

	public static void main(String[] args) throws ParseException, IOException
	{
		int port = 8082;
		URI rootUri = URI.create("root");
		String acl = "users.acl.txt";
		
		CommandLine commandLine = hello(args);
		
		if (commandLine.hasOption(Args.Port))
		{
			port = Integer.valueOf(commandLine.getOptionValue(Args.Port));
		}
		
		if (commandLine.hasOption(Args.Root))
		{
			rootUri = URI.create(commandLine.getOptionValue(Args.Root));
		}
		
		if (commandLine.hasOption(Args.Acl))
		{
			acl = commandLine.getOptionValue(Args.Acl);
		}
		
		if (!rootUri.isAbsolute())
		{
			rootUri = new File(".").getCanonicalFile().toURI().resolve(rootUri);
		}
		
		System.out.println("Root: '" + rootUri.toString() + "'");
		System.out.println("Port: '" + port + "'");
		System.out.println("Acl : '" + acl + "'");
		
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getClients().add(Protocol.FILE);
		component.getDefaultHost()
			.attach(new MyApp(component.getContext().createChildContext(), rootUri.toString(), acl));

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
