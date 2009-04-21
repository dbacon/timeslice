package bacond.timeslicer.app.auth;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;


public class AclFileTest
{
	@Test
	public void hello()
	{
		assertArrayEquals("testpass".toCharArray(), new AclFile("test-input/auth/test.acl").resolve("test1"));
	}
}
