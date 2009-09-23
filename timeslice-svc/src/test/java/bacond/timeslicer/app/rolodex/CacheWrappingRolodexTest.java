package bacond.timeslicer.app.rolodex;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;


public class CacheWrappingRolodexTest
{
	public static class MemoryRolodex implements IRolodex
	{
		private final Set<ClientInfo> names = new LinkedHashSet<ClientInfo>();

		@Override
		public void addClientInfo(ClientInfo clientInfo)
		{
			names.add(clientInfo);
		}

		@Override
		public List<ClientInfo> getClientInfos()
		{
			return Collections.unmodifiableList(new ArrayList<ClientInfo>(names));
		}

		@Override
		public boolean isWritable()
		{
			return true;
		}
	}

	public static class TestMemoryRolodex extends MemoryRolodex
	{
		private int callCount = 0;

		@Override
		public List<ClientInfo> getClientInfos()
		{
			++callCount;
			return super.getClientInfos();
		}

		public int getCallCount()
		{
			return callCount;
		}

		public void setCallCount(int callCount)
		{
			this.callCount = callCount;
		}
	}

	@Test
	public void test_0() throws Exception
	{
		TestMemoryRolodex memoryRolodex = new TestMemoryRolodex();
		CacheWrappingRolodex cachingRolodex = new CacheWrappingRolodex(memoryRolodex);

		assertEquals(Collections.emptyList(), cachingRolodex.getClientInfos());

		cachingRolodex.addClientInfo(new ClientInfo("dave"));

		assertEquals(Arrays.asList(new ClientInfo("dave")), cachingRolodex.getClientInfos());

		int origCalls = memoryRolodex.getCallCount();
		cachingRolodex.invalidate();
		assertEquals(Arrays.asList(new ClientInfo("dave")), cachingRolodex.getClientInfos());
		assertEquals(Arrays.asList(new ClientInfo("dave")), cachingRolodex.getClientInfos());
		assertEquals(Arrays.asList(new ClientInfo("dave")), cachingRolodex.getClientInfos());
		assertEquals(1 + origCalls, memoryRolodex.getCallCount());

		origCalls = memoryRolodex.getCallCount();
		cachingRolodex.invalidate();
		assertEquals(Arrays.asList(new ClientInfo("dave")), cachingRolodex.getClientInfos());
		assertEquals(1 + origCalls, memoryRolodex.getCallCount());
	}
}
