package bacond.timeslicer.app.rolodex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CacheWrappingRolodex implements IRolodex
{
	private final IRolodex rolodex;
	private final List<ClientInfo> cachedNames = new ArrayList<ClientInfo>();
	boolean valid = false;

	public CacheWrappingRolodex(IRolodex rolodex)
	{
		this.rolodex = rolodex;
	}

	public IRolodex getRolodex()
	{
		return rolodex;
	}

	@Override
	public synchronized void addClientInfo(ClientInfo clientInfo)
	{
		getRolodex().addClientInfo(clientInfo);
		invalidate();
	}

	public void invalidate()
	{
		valid = false;
	}

	public boolean isValid()
	{
		return valid;
	}

	protected List<ClientInfo> getCachedNames()
	{
		return cachedNames;
	}

	protected synchronized void reload()
	{
		if (!isValid())
		{
			getCachedNames().clear();
			getCachedNames().addAll(getRolodex().getClientInfos());
			valid = true;
		}
	}

	@Override
	public List<ClientInfo> getClientInfos()
	{
		reload();
		return Collections.unmodifiableList(getCachedNames());
	}

	@Override
	public boolean isWritable()
	{
		return getRolodex().isWritable();
	}
}
