package bacond.timeslicer.app.core;

import org.junit.Assert;
import org.junit.Test;

import bacond.timeslicer.app.core.AclFile;

public class AclFileTest
{
	@Test
	public void hello()
	{
	    Assert.assertEquals("testpass", new AclFile("test-input/auth/test.acl").lookupPassword("test1"));
	}
}
