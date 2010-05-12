package bacond.timeslice.web.gwt.server.rpc;

import java.util.ArrayList;
import java.util.Collection;

import bacond.lib.util.ITransform;

public class Transform
{
	public static <R, D> Collection<D> tr(Collection<R> xs, ITransform<R, D> f)
	{
		ArrayList<D> result = new ArrayList<D>();

		// no ordering, could be parallelized.
		for (R x: xs)
		{
			result.add(f.apply(x));
		}

		return result;
	}
}
