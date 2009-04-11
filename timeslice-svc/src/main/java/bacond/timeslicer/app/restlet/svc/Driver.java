package bacond.timeslicer.app.restlet.svc;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Driver
{
	private Integer port = 8082;
	private String acl = "users.acl.txt";
	private URI rootUri = URI.create("root");
	private String safeDir = null;
	private boolean doPreload = false;

	public static class Args
	{
		public static final String Root = "root";
		public static final String Port = "port";
		public static final String Acl = "acl";
		public static final String SafeDir = "safedir";
		public static final String PreLoad = "preload";
	}
	
	private Driver configureFromCommandline(String[] args)
	{
		Options opts = new Options();
		opts.addOption(null, Args.Port, true, "Listening port.");
		opts.addOption(null, Args.Root, true, "Path of shared folder.");
		opts.addOption(null, Args.Acl, true, "ACL filename.");
		opts.addOption(null, Args.SafeDir, true, "Safe dir in which updates can be installed an run.");
		opts.addOption(null, Args.PreLoad, false, "Pre-load entries.");
		
		CommandLine commandLine = null;
		
		try
		{
			commandLine = new GnuParser().parse(opts, args);
		}
		catch (ParseException e)
		{
			System.err.println("Command-line parsing failed: " + e.getMessage());
		}
		
		if (null != commandLine)
		{
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

			if (commandLine.hasOption(Args.SafeDir))
			{
				safeDir = commandLine.getOptionValue(Args.SafeDir);
			}

			doPreload = commandLine.hasOption(Args.PreLoad);
		}
		
		return this;
	}

	private Driver configureFromSettingsProvider()
	{
		Properties settings = new GenericSettingsProvider().readSettings();

		port = Integer.valueOf(settings.getProperty("timeslice." + Args.Port, "" + port));
		acl = settings.getProperty("timeslice." + Args.Acl, acl);
		rootUri = URI.create(settings.getProperty("timeslice." + Args.Root, "root"));
		safeDir = settings.getProperty("timeslice." + Args.SafeDir, safeDir);

		return this;
	}

	public Driver fixRootUri() throws IOException
	{
		if (!rootUri.isAbsolute())
		{
			rootUri = new File(".").getCanonicalFile().toURI().resolve(rootUri);
		}

		return this;
	}

	public Driver printInfo(PrintStream out)
	{
		out.println("Service settings:");
		out.println("  Root: '" + rootUri.toString() + "'");
		out.println("  Port: '" + port + "'");
		out.println("  Acl : '" + acl + "'");
		out.println("  Safe: '" + safeDir + "'");
		out.println("  Seed: '" + (doPreload ? "true" : "false") + "'");

		return this;
	}

	public void runProgram()
	{
		new Program(port, rootUri, acl, safeDir).run(doPreload);
	}

	public static void main(String[] args) throws ParseException, IOException
	{
		new Driver()
			.configureFromSettingsProvider()
			.configureFromCommandline(args)
			.fixRootUri()
			.printInfo(System.out)
			.runProgram();
	}
}
