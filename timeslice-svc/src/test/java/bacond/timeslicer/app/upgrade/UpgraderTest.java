package bacond.timeslicer.app.upgrade;

import java.io.File;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class UpgraderTest
{
	@Test
	public void test0() throws Exception
	{
		//String url = "http://timeslice.googlecode.com/svn/wiki/LatestRelease.wiki";
		String url = "file:test-input/remote/releases.txt";

		Upgrader upgrader = new Upgrader(url, "/tmp/");

		UpgradeInfo latestUpgradeInfo = upgrader.findLatestAcceptable(Collections.<String>emptyList(), upgrader.getLatestUpgradeInfo());

		//
		// Avoid downloading every time during testing.
		//
		File localFile = new File(new File(latestUpgradeInfo.getDownloadUrl().getPath()).getName());

		if (localFile.exists())
		{
			System.out.println("Found existing resource, using: " + localFile.toString());

			// TODO: download checksum and verify to be able to trust this.
			// otherwise, should delete as below.

			// FileUtils.deleteQuietly(localFile);
			// FileUtils.deleteDirectory(localFile);
		}
		else
		{
			localFile = upgrader.download(latestUpgradeInfo);
			System.out.println("Existing resource not found, downloading: " + localFile.toString());
		}

		File rootDir = upgrader.extract(localFile);

		String cmd = "sh bin/runme.sh";
		System.out.printf("Running '%s' in directory '%s'.", cmd, rootDir.getCanonicalPath());
	}
}
