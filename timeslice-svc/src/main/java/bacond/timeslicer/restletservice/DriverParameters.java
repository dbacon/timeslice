package bacond.timeslicer.restletservice;

import java.net.URI;

import org.apache.commons.cli.CommandLine;

public class DriverParameters
{
	public static class Default
	{
		public static final String UpdateUrl = "file:/dev/null";
		public static final boolean DoPreload = false;
		public static final URI RootUri = URI.create("root");
		public static final String Acl = "users.acl.txt";
		public static final int Port = 8082;
	}

	private Integer port = Default.Port;
	private String acl = Default.Acl;
	private String rcFilename;
	private URI rootUri = Default.RootUri;
	private String safeDir;
	private boolean doPreload = Default.DoPreload;
	private String updateUrl = Default.UpdateUrl;
	private String proxyHost;
	private String proxyPort;
	private String nonProxyHosts;
	private CommandLine commandLine;

	public Integer getPort()
	{
		return port;
	}
	public void setPort(Integer port)
	{
		this.port = port;
	}
	public String getAcl()
	{
		return acl;
	}
	public void setAcl(String acl)
	{
		this.acl = acl;
	}
	public String getRcFilename()
	{
		return rcFilename;
	}
	public void setRcFilename(String rcFilename)
	{
		this.rcFilename = rcFilename;
	}
	public URI getRootUri()
	{
		return rootUri;
	}
	public void setRootUri(URI rootUri)
	{
		this.rootUri = rootUri;
	}
	public String getSafeDir()
	{
		return safeDir;
	}
	public void setSafeDir(String safeDir)
	{
		this.safeDir = safeDir;
	}
	public boolean isDoPreload()
	{
		return doPreload;
	}
	public void setDoPreload(boolean doPreload)
	{
		this.doPreload = doPreload;
	}
	public String getUpdateUrl()
	{
		return updateUrl;
	}
	public void setUpdateUrl(String updateUrl)
	{
		this.updateUrl = updateUrl;
	}
	public String getProxyHost()
	{
		return proxyHost;
	}
	public void setProxyHost(String proxyHost)
	{
		this.proxyHost = proxyHost;
	}
	public String getProxyPort()
	{
		return proxyPort;
	}
	public void setProxyPort(String proxyPort)
	{
		this.proxyPort = proxyPort;
	}
	public String getNonProxyHosts()
	{
		return nonProxyHosts;
	}
	public void setNonProxyHosts(String nonProxyHosts)
	{
		this.nonProxyHosts = nonProxyHosts;
	}
	public CommandLine getCommandLine()
	{
		return commandLine;
	}
	public void setCommandLine(CommandLine commandLine)
	{
		this.commandLine = commandLine;
	}


}