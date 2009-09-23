package bacond.timeslicer.app.upgrade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class Upgrader
{
	private String url;
	private String safeDir;

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getSafeDir()
	{
		return safeDir;
	}

	public void setSafeDir(String safeDir)
	{
		this.safeDir = safeDir;
	}

	public Upgrader(String url, String safeDir)
	{
		this.url = url;
		this.safeDir = safeDir;
	}

	public File download(UpgradeInfo info) throws Exception
	{
		File localFile = new File(FilenameUtils.concat(getSafeDir(), new File(info.getDownloadUrl().getPath()).getName()));

		int nread = IOUtils.copy(info.getDownloadUrl().openStream(), new FileOutputStream(localFile));
		System.out.println("read: " + nread);
		return localFile;
	}

	public File extract(File downloadedFile) throws Exception
	{
		File rootDir = null;

		ZipFile zipFile = new ZipFile(downloadedFile);
		Enumeration<? extends ZipEntry> entryEnum = zipFile.entries();
		while (entryEnum.hasMoreElements())
		{
			ZipEntry entry = entryEnum.nextElement();

			if (entry.getName().endsWith("/"))
			{
				File dir = new File(FilenameUtils.concat(getSafeDir(), entry.getName()));

				if (entry.getName().lastIndexOf('/') == entry.getName().indexOf('/'))
				{
					if (null == rootDir)
					{
						rootDir = dir;
					}
					else
					{
						throw new RuntimeException("More than one folder in the root of zipfile.");
					}
				}

				if (dir.exists())
				{
					if (true)
					{
						System.out.println("Found existing directory, deleting: " + dir.toString());
						FileUtils.deleteDirectory(dir);
					}
					else
					{
						throw new RuntimeException("Directory already exists, not deleting: " + dir.toString());
					}
				}

				System.out.println("Creating directory : " + dir.toString());
				dir.mkdirs();
			}
			else
			{
				File file = new File(FilenameUtils.concat(getSafeDir(), entry.getName()));

				if (file.exists())
				{
					throw new RuntimeException("File already exists: " + file.toString());
				}

				System.out.println("Extracting file    : " + file.toString());
				IOUtils.copy(zipFile.getInputStream(entry), new FileOutputStream(file));
			}
		}

		return rootDir;
	}

	public List<UpgradeInfo> getLatestUpgradeInfo() throws Exception
	{
		List<UpgradeInfo> upgrades = new ArrayList<UpgradeInfo>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(getUrl()).openStream()));

		while (true)
		{
			String line = reader.readLine();

			if (null == line)
			{
				break;
			}

			String[] pieces = line.split(Pattern.quote("||"));
			if (pieces.length > 2)
			{
				DateTime when = ISODateTimeFormat.dateTime().parseDateTime(pieces[1]);
				String url = pieces[2];

				UpgradeInfo info = new UpgradeInfo(when.toInstant(), new URL(url));

				if (pieces.length > 3)
				{
					info.getTags().addAll(Arrays.asList(pieces[3].split(",")));
				}

				upgrades.add(info);
			}
		}

		return upgrades;
	}

	public UpgradeInfo findLatestAcceptable(List<String> requiredTags, List<UpgradeInfo> versions)
	{
		UpgradeInfo newestAcceptable = null;

		for (UpgradeInfo info: versions)
		{
			if (null == requiredTags || info.getTags().containsAll(requiredTags))
			{
				if (null == newestAcceptable)
				{
					newestAcceptable = info;
				}
				else
				{
					if (info.getReleaseTime().isAfter(newestAcceptable.getReleaseTime()))
					{
						newestAcceptable = info;
					}
				}
			}
		}

		return newestAcceptable;
	}

}
