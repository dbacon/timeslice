package bacond.timeslicer.app.generic;

import java.util.Collection;


public class Listing<T extends IListable>
{
	private final Collection<T> items;

	private final String location;

	public Listing(String location, Collection<T> items)
	{
		this.location = location;
		this.items = items;
	}

	public Collection<T> getItems()
	{
		return items;
	}

	public String getLocation()
	{
		return location;
	}
}
