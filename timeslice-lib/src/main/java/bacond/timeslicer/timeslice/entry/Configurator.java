package bacond.timeslicer.timeslice.entry;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Configurator
{
	private final StartupParameters startupParams;

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

	public Configurator(StartupParameters startupParams)
	{
        this.startupParams = startupParams;
	}

	public StartupParameters getStartupParams()
	{
		return startupParams;
	}

	public Configurator readCommandLineArgs(String[] args)
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
			getStartupParams().setCommandLine(new GnuParser().parse(opts, args));
		}
		catch (ParseException e)
		{
			System.err.println("Command-line parsing failed: " + e.getMessage());
		}

		return this;
	}

	public Configurator configureFromCommandlineBootItems()
	{
		if (null != getStartupParams().getCommandLine())
		{
			if (getStartupParams().getCommandLine().hasOption(Args.RcFile))
			{
				getStartupParams().setRcFilename(getStartupParams().getCommandLine().getOptionValue(Args.RcFile));
			}
		}

		return this;
	}

	public Configurator configureFromCommandline()
	{
		if (null != getStartupParams().getCommandLine())
		{
			if (getStartupParams().getCommandLine().hasOption(Args.Port))
			{
				getStartupParams().setPort(Integer.valueOf(getStartupParams().getCommandLine().getOptionValue(Args.Port)));
			}

			if (getStartupParams().getCommandLine().hasOption(Args.Root))
			{
				getStartupParams().setRootUri(URI.create(getStartupParams().getCommandLine().getOptionValue(Args.Root)));
			}

			if (getStartupParams().getCommandLine().hasOption(Args.Acl))
			{
				getStartupParams().setAcl(getStartupParams().getCommandLine().getOptionValue(Args.Acl));
			}

			if (getStartupParams().getCommandLine().hasOption(Args.SafeDir))
			{
				getStartupParams().setSafeDir(getStartupParams().getCommandLine().getOptionValue(Args.SafeDir));
			}

			getStartupParams().setDoPreload(getStartupParams().getCommandLine().hasOption(Args.PreLoad));

			if (getStartupParams().getCommandLine().hasOption(Args.UpdateUrl))
			{
				getStartupParams().setUpdateUrl(getStartupParams().getCommandLine().getOptionValue(Args.UpdateUrl));
			}
		}

		return this;
	}

	public Configurator configureFromRc(File file)
	{
        if (null != file)
        {
            if (!getStartupParams().getCommandLine().hasOption(Args.NoRc))
            {
                configureFromSettingsProvider(createFileSettingsProvider(file.toString()));
            }
        }

        return this;
	}

	public Configurator configureFromRc()
	{
		if (!getStartupParams().getCommandLine().hasOption(Args.NoRc))
		{
			configureFromSettingsProvider(createFileSettingsProvider(getStartupParams().getRcFilename()));
		}

		return this;
	}

	public ISettingsProvider createFileSettingsProvider(String rcFilename)
	{
		return new GenericSettingsProvider(rcFilename);
	}

	public void configureFromSettingsProvider(ISettingsProvider settingsProvider)
	{
		Properties settings = new Properties();

		settingsProvider.readSettings(settings);

		getStartupParams().setPort(Integer.valueOf(settings.getProperty("timeslice." + Args.Port, "" + getStartupParams().getPort())));
		getStartupParams().setAcl(settings.getProperty("timeslice." + Args.Acl, getStartupParams().getAcl()));
		getStartupParams().setRootUri(URI.create(settings.getProperty("timeslice." + Args.Root, "root")));
		getStartupParams().setSafeDir(settings.getProperty("timeslice." + Args.SafeDir, getStartupParams().getSafeDir()));
		getStartupParams().setUpdateUrl(settings.getProperty("timeslice." + Args.UpdateUrl, getStartupParams().getUpdateUrl()));
	}

	public Configurator fixRootUri() throws IOException
	{
		if (!getStartupParams().getRootUri().isAbsolute())
		{
			getStartupParams().setRootUri(new File(".").getCanonicalFile().toURI().resolve(getStartupParams().getRootUri()));
		}

		return this;
	}

	public Configurator printInfo(PrintStream out)
	{
		out.println("Service settings:");
		out.println("  Root    : '" + getStartupParams().getRootUri().toString() + "'");
		out.println("  Port    : '" + getStartupParams().getPort() + "'");
		out.println("  Acl     : '" + getStartupParams().getAcl() + "'");
		out.println("  Safe    : '" + getStartupParams().getSafeDir() + "'");
		out.println("  Preload : '" + (getStartupParams().isDoPreload() ? "true" : "false") + "'");
		out.println("  Update  : '" + getStartupParams().getUpdateUrl() + "'");

		return this;
	}

	private static void importSystemProperty(String key, String value)
	{
		if (null != value)
		{
			System.setProperty(key, value);
		}
	}

	public Configurator applyProxySettingsToSystem()
	{
		importSystemProperty("http.proxyHost", getStartupParams().getProxyHost());
		importSystemProperty("http.proxyPort", getStartupParams().getProxyPort());
		importSystemProperty("http.nonProxyHosts", getStartupParams().getNonProxyHosts());

		return this;
	}

    public StartupParameters standardBoot() throws ParseException, IOException
    {
        return standardBoot(null);
    }

    public StartupParameters standardBoot(File file) throws IOException
    {
        return configureFromRc(file)
            .applyProxySettingsToSystem()
            .fixRootUri()
            .printInfo(System.out)
            .getStartupParams();
    }

    public StartupParameters standardBootCli(String[] args) throws ParseException, IOException
    {
        return readCommandLineArgs(args)
            .configureFromCommandlineBootItems()
            .configureFromRc()
            .configureFromCommandline()
            .applyProxySettingsToSystem()
            .fixRootUri()
            .printInfo(System.out)
            .getStartupParams();
    }

	public static void main(IRuntimeContainer container, String[] args) throws ParseException, IOException
	{
	    container.runProgram(new Configurator(new StartupParameters()).standardBootCli(args));
	}
}
