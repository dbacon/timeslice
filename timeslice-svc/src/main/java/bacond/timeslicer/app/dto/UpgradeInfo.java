package bacond.timeslicer.app.dto;

public class UpgradeInfo
{
	private String releaseTime;
	private String downloadUrl;

	public UpgradeInfo()
	{
		this("", "");
	}

	public UpgradeInfo(String releaseTime, String downloadUrl)
	{
		this.releaseTime = releaseTime;
		this.downloadUrl = downloadUrl;
	}

	public String getReleaseTime()
	{
		return releaseTime;
	}

	public void setReleaseTime(String releaseTime)
	{
		this.releaseTime = releaseTime;
	}

	public String getDownloadUrl()
	{
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl)
	{
		this.downloadUrl = downloadUrl;
	}
}
