package bacond.timeslicer.app.rolodex;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;


public class FileRolodexTest
{
	/**
	 * Make sure a new file show an empty list,
	 * and adding an entry is retrievable.
	 *
	 * @throws Exception
	 */
	@Test
	public void test_0() throws Exception
	{
		File tmpFile = File.createTempFile("junit-test-", ".rolodex");
		FileRolodex rolodex = new FileRolodex(tmpFile);

		assertEquals(Collections.emptyList(), rolodex.getClientInfos());

		rolodex.addClientInfo(new ClientInfo("Dave Bacon"));

		assertEquals(Arrays.asList(new ClientInfo("Dave Bacon")), rolodex.getClientInfos());
	}

	/**
	 * Make sure names with new-lines are disallowed.
	 *
	 * @throws Exception
	 */
	@Test(expected=RuntimeException.class)
	public void test_1() throws Exception
	{
		File tmpFile = File.createTempFile("junit-test-", ".rolodex");
		FileRolodex rolodex = new FileRolodex(tmpFile);

		assertEquals(Collections.emptyList(), rolodex.getClientInfos());

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("abc");
		pw.print("def");
		pw.flush();

		rolodex.addClientInfo(new ClientInfo(sw.toString()));
	}
}
