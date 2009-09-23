package bacond.timeslicer.app.generic;

import java.util.List;

public interface IStore<T>
{
	T findItem(String key);
	List<T> listItems();
}
