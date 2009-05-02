package bacond.timeslicer.app.restlet.svc;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import bacond.timeslicer.app.restlet.svc.DriverParameters.Default;


public class DriverTest
{
	private static class MockedDriver extends Driver
	{
		@Override
		public void runProgram()
		{
			// super.runProgram() does:
			//   new Program(port, rootUri, acl, safeDir, updateUrl).run(doPreload);
			// Suppress this and let tests do assertions only.
		}
	}

	/**
	 * Tests defaults.
	 *
	 * @throws Exception
	 */
	@Test
	public void test_0() throws Exception
	{
		String[] args = new String[] { "--no-rc" };

		MockedDriver driver = new MockedDriver();
		Driver.main(driver, args);
		DriverParameters params = driver.getDriverParams();

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

		MockedDriver driver = new MockedDriver();
		Driver.main(driver, args);

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

		MockedDriver driver = new MockedDriver();
		Driver.main(driver, args);
		DriverParameters params = driver.getDriverParams();

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

		MockedDriver driver = new MockedDriver();
		Driver.main(driver, args);
		DriverParameters params = driver.getDriverParams();

		assertEquals("test-input/driver/example-rc-1.txt", params.getRcFilename());
		assertEquals(Integer.valueOf(1111), params.getPort());
		assertEquals("test-acl.acl", params.getAcl());
//		assertEquals("test-root", driver.getRootUri());
		assertEquals("test-safedir", params.getSafeDir());
		assertEquals("test://url/", params.getUpdateUrl());
	}
}
