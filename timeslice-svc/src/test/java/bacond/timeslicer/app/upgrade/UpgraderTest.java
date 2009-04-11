package bacond.timeslicer.app.upgrade;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import bacond.timeslicer.app.upgrade.UpgradeInfo;
import bacond.timeslicer.app.upgrade.Upgrader;

@Ignore
public class UpgraderTest
{
	@Test
	public void test0() throws Exception
	{
		Upgrader upgrader = new Upgrader("/tmp/");

		UpgradeInfo latestUpgradeInfo = upgrader.getLatestUpgradeInfo();

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
