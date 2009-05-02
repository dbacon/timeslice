package bacond.timeslicer.app.upgrade;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;

public class UpgradeInfo
{
	private Instant releaseTime;
	private URL downloadUrl;
	private List<String> tags = new ArrayList<String>();

	public UpgradeInfo(Instant releaseTime, URL downloadUrl)
	{
		this.releaseTime = releaseTime;
		this.downloadUrl = downloadUrl;
	}

	public Instant getReleaseTime()
	{
		return releaseTime;
	}

	public void setReleaseTime(Instant releaseTime)
	{
		this.releaseTime = releaseTime;
	}

	public URL getDownloadUrl()
	{
		return downloadUrl;
	}

	public void setDownloadUri(URL downloadUrl)
	{
		this.downloadUrl = downloadUrl;
	}

	public List<String> getTags()
	{
		return tags;
	}

	public void setTags(List<String> tags)
	{
		this.tags = tags;
	}

}