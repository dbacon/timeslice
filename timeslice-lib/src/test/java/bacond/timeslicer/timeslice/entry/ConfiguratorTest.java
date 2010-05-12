package bacond.timeslicer.timeslice.entry;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import bacond.timeslicer.timeslice.entry.StartupParameters.Default;

public class ConfiguratorTest
{
	/**
	 * Tests defaults.
	 *
	 * @throws Exception
	 */
	@Test
	public void test_0() throws Exception
	{
		String[] args = new String[] { "--no-rc" };

		StartupParameters params = new Configurator(new StartupParameters()).standardBootCli(args);

		assertEquals(Default.Acl, params.getAcl());
		assertEquals(null, params.getNonProxyHosts());
		assertEquals(null, params.getProxyHost());
		assertEquals(null, params.getProxyPort());
		assertEquals(null, params.getRcFilename());
//		assertEquals(Default.RootUri, driver.getRootUri());
		assertEquals(null, params.getSafeDir());
		assertEquals(Default.UpdateUrl, params.getUpdateUrl());
	}

	/**
	 * Need to cover test case of rc file being auto-calculated,
	 * but this is platform-specific
	 * and not quite available for testing yet.
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void test_11() throws Exception
	{
		String[] args = new String[] {  };

		new Configurator(new StartupParameters()).standardBootCli(args);

//		assertEquals("/home/dbacon/.timeslicerc", driver.getRcFilename());
	}

	/**
	 * Tests reading settings from properties file.
	 *
	 * @throws Exception
	 */
	@Test
	public void test_1() throws Exception
	{
		String[] args = new String[] { "--rc", "test-input/driver/example-rc-1.txt" };

		StartupParameters params = new Configurator(new StartupParameters()).standardBootCli(args);

		assertEquals("test-input/driver/example-rc-1.txt", params.getRcFilename());
		assertEquals(Integer.valueOf(9988), params.getPort());
		assertEquals("test-acl.acl", params.getAcl());
//		assertEquals("test-root", driver.getRootUri());
		assertEquals("test-safedir", params.getSafeDir());
		assertEquals("test://url/", params.getUpdateUrl());
	}

	/**
	 * Tests command-line overrides of settings.
	 *
	 * @throws Exception
	 */
	@Test
	public void test_2() throws Exception
	{
		String[] args = new String[]
           {
				"--rc", "test-input/driver/example-rc-1.txt",
				"--port", "1111"
			};

		StartupParameters params = new Configurator(new StartupParameters()).standardBootCli(args);

		assertEquals("test-input/driver/example-rc-1.txt", params.getRcFilename());
		assertEquals(Integer.valueOf(1111), params.getPort());
		assertEquals("test-acl.acl", params.getAcl());
//		assertEquals("test-root", driver.getRootUri());
		assertEquals("test-safedir", params.getSafeDir());
		assertEquals("test://url/", params.getUpdateUrl());
	}
}
