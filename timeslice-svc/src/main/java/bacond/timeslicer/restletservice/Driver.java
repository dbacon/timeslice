package bacond.timeslicer.restletservice;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Driver
{
	private final DriverParameters driverParams = new DriverParameters();

	public static class Args
	{
		public static final String Root = "root";
		public static final String Port = "port";
		public static final String Acl = "acl";
		public static final String SafeDir = "safedir";
		public static final String PreLoad = "preload";
		public static final String UpdateUrl = "update-url";
		public static final String NoRc = "no-rc";
		public static final String RcFile = "rc";
	}

	public DriverParameters getDriverParams()
	{
		return driverParams;
	}

	private Driver readCommandLineArgs(String[] args)
	{
		Options opts = new Options();
		opts.addOption(null, Args.Port, true, "Listening port.");
		opts.addOption(null, Args.Root, true, "Path of shared folder.");
		opts.addOption(null, Args.Acl, true, "ACL filename.");
		opts.addOption(null, Args.SafeDir, true, "Safe dir in which updates can be installed an run.");
		opts.addOption(null, Args.PreLoad, false, "Pre-load entries.");
		opts.addOption(null, Args.NoRc, false, "Do not load any rc file.");
		opts.addOption(null, Args.RcFile, true, "Load specified rc file instead of default.");

		try
		{
			getDriverParams().setCommandLine(new GnuParser().parse(opts, args));
		}
		catch (ParseException e)
		{
			System.err.println("Command-line parsing failed: " + e.getMessage());
		}

		return this;
	}

	private Driver configureFromCommandlineBootItems()
	{
		if (null != getDriverParams().getCommandLine())
		{
			if (getDriverParams().getCommandLine().hasOption(Args.RcFile))
			{
				getDriverParams().setRcFilename(getDriverParams().getCommandLine().getOptionValue(Args.RcFile));
			}
		}

		return this;
	}

	private Driver configureFromCommandline()
	{
		if (null != getDriverParams().getCommandLine())
		{
			if (getDriverParams().getCommandLine().hasOption(Args.Port))
			{
				getDriverParams().setPort(Integer.valueOf(getDriverParams().getCommandLine().getOptionValue(Args.Port)));
			}

			if (getDriverParams().getCommandLine().hasOption(Args.Root))
			{
				getDriverParams().setRootUri(URI.create(getDriverParams().getCommandLine().getOptionValue(Args.Root)));
			}

			if (getDriverParams().getCommandLine().hasOption(Args.Acl))
			{
				getDriverParams().setAcl(getDriverParams().getCommandLine().getOptionValue(Args.Acl));
			}

			if (getDriverParams().getCommandLine().hasOption(Args.SafeDir))
			{
				getDriverParams().setSafeDir(getDriverParams().getCommandLine().getOptionValue(Args.SafeDir));
			}

			getDriverParams().setDoPreload(getDriverParams().getCommandLine().hasOption(Args.PreLoad));

			if (getDriverParams().getCommandLine().hasOption(Args.UpdateUrl))
			{
				getDriverParams().setUpdateUrl(getDriverParams().getCommandLine().getOptionValue(Args.UpdateUrl));
			}
		}

		return this;
	}

	private Driver configureFromRc()
	{
		if (!getDriverParams().getCommandLine().hasOption(Args.NoRc))
		{
			configureFromSettingsProvider(createFileSettingsProvider(getDriverParams().getRcFilename()));
		}

		return this;
	}

	public ISettingsProvider createFileSettingsProvider(String rcFilename)
	{
		return new GenericSettingsProvider(rcFilename);
	}

	private void configureFromSettingsProvider(ISettingsProvider settingsProvider)
	{
		Properties settings = new Properties();

		settingsProvider.readSettings(settings);

		getDriverParams().setPort(Integer.valueOf(settings.getProperty("timeslice." + Args.Port, "" + getDriverParams().getPort())));
		getDriverParams().setAcl(settings.getProperty("timeslice." + Args.Acl, getDriverParams().getAcl()));
		getDriverParams().setRootUri(URI.create(settings.getProperty("timeslice." + Args.Root, "root")));
		getDriverParams().setSafeDir(settings.getProperty("timeslice." + Args.SafeDir, getDriverParams().getSafeDir()));
		getDriverParams().setUpdateUrl(settings.getProperty("timeslice." + Args.UpdateUrl, getDriverParams().getUpdateUrl()));
	}

	public Driver fixRootUri() throws IOException
	{
		if (!getDriverParams().getRootUri().isAbsolute())
		{
			getDriverParams().setRootUri(new File(".").getCanonicalFile().toURI().resolve(getDriverParams().getRootUri()));
		}

		return this;
	}

	public Driver printInfo(PrintStream out)
	{
		out.println("Service settings:");
		out.println("  Root    : '" + getDriverParams().getRootUri().toString() + "'");
		out.println("  Port    : '" + getDriverParams().getPort() + "'");
		out.println("  Acl     : '" + getDriverParams().getAcl() + "'");
		out.println("  Safe    : '" + getDriverParams().getSafeDir() + "'");
		out.println("  Preload : '" + (getDriverParams().isDoPreload() ? "true" : "false") + "'");
		out.println("  Update  : '" + getDriverParams().getUpdateUrl() + "'");

		return this;
	}

	private static void importSystemProperty(String key, String value)
	{
		if (null != value)
		{
			System.setProperty(key, value);
		}
	}

	public Driver applyProxySettingsToSystem()
	{
		importSystemProperty("http.proxyHost", getDriverParams().getProxyHost());
		importSystemProperty("http.proxyPort", getDriverParams().getProxyPort());
		importSystemProperty("http.nonProxyHosts", getDriverParams().getNonProxyHosts());

		return this;
	}

	public void runProgram()
	{
		new Program(
				getDriverParams().getPort(),
				getDriverParams().getRootUri(),
				getDriverParams().getAcl(),
				getDriverParams().getSafeDir(),
				getDriverParams().getUpdateUrl())
			.run(getDriverParams().isDoPreload());
	}

	public static void main(Driver driver, String[] args) throws ParseException, IOException
	{
		driver
			.readCommandLineArgs(args)
			.configureFromCommandlineBootItems()
			.configureFromRc()
			.configureFromCommandline()
			.applyProxySettingsToSystem()
			.fixRootUri()
			.printInfo(System.out)
			.runProgram();
	}

	public static void main(String[] args) throws ParseException, IOException
	{
		main(new Driver(), args);
	}
}
