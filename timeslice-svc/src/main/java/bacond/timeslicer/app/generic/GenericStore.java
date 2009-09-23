package bacond.timeslicer.app.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import bacond.lib.util.Check;

public class GenericStore<T extends IHasWhen>
{
	private final Map<Instant, T> store = new LinkedHashMap<Instant, T>();

	private Map<Instant, T> getStoreInternal()
	{
		return store;
	}

	public boolean contains(Object key)
	{
		return getStoreInternal().containsKey(key);
	}

	public T remove(Object key)
	{
		return getStoreInternal().remove(key);
	}

	public T find(Object key)
	{
		return getStoreInternal().get(key);
	}

	public Collection<T> getAllItems()
	{
		return getStoreInternal().values();
	}

	public void enterTag(T t)
	{
		getStoreInternal().put(t.getWhen(), t);
	}

	public void enterAllTags(Collection<? extends T> all)
	{
		for (T tag: all)
		{
			enterTag(tag);
		}
	}

	public List<T> getItemsConstrained(Instant minInstant, Instant maxInstant, boolean sortReverse, int pageSize, int pageIndex)
	{
		Check.notNull(minInstant, "minimum instant");
		Check.notNull(maxInstant, "maximum instant");

		List<T> items = new ArrayList<T>(getAllItems());
		List<T> itemsInRange = new ArrayList<T>();

		for (T item: items)
		{
			if (true
					&& (item.getWhen().isEqual(minInstant) || item.getWhen().isAfter(minInstant))
					&& (item.getWhen().isEqual(maxInstant) || item.getWhen().isBefore(maxInstant)))
			{
				itemsInRange.add(item);
			}
		}

		Comparator<T> cmp = new CompareByTime<T>();

		if (sortReverse)
		{
			cmp = Collections.reverseOrder(cmp);
		}

		Collections.sort(itemsInRange, cmp);

		if (pageIndex < 0)
		{
			throw new RuntimeException("Page Index cannot be negative.");
		}

		if (pageSize <= 0)
		{
			throw new RuntimeException("Page Size is not positive.");
		}

		// pageIndex 0..maxint
		// pagesize 1..maxint

		return itemsInRange.subList(Math.min(itemsInRange.size(), pageIndex*pageSize), Math.min(itemsInRange.size(), (pageIndex + 1)*pageSize));
	}

}
