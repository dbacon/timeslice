package bacond.timeslicer.app.upgrade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

public class Upgrader
{
	private String safeDir;

	public String getSafeDir()
	{
		return safeDir;
	}

	public void setSafeDir(String safeDir)
	{
		this.safeDir = safeDir;
	}

	public Upgrader(String safeDir)
	{
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

	public UpgradeInfo getLatestUpgradeInfo() throws Exception
	{
		Instant newest = new Instant(0);
		String newestUri = "";

		String inputUrlString = "http://timeslice.googlecode.com/svn/wiki/LatestRelease.wiki";
//			String inputUrlString = "file:test-input/remote/releases.txt";

		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(inputUrlString).openStream()));

		while (true)
		{
			String line = reader.readLine();

			if (null == line)
			{
				break;
			}


			String[] pieces = line.split(Pattern.quote("||"));
			if (pieces.length >= 2)
			{
				DateTime when = ISODateTimeFormat.dateTime().parseDateTime(pieces[1]);
				String url = pieces[2];

				if (when.isAfter(newest))
				{
					newest = when.toInstant();
					newestUri = url;
				}
			}
		}

		URL url = new URL(newestUri);
		System.out.printf("newest is at %s: %s\n", newest, url.toString());

		return new UpgradeInfo(newest, url);
	}
}