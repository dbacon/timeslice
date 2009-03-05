package bacond.lib.util;

public class Transforms
{
	public static <R, D> ITransform<R, D> invalid()
	{
		return new ITransform<R, D>()
		{
			@Override
			public D apply(R r)
			{
				throw new RuntimeException("Invalid transform used.");
			}
		};
	}
	
	public static <T> T mapNullTo(T t, T altT)
	{
		if (null == t)
		{
			return altT;
		}
		else
		{
			return t;
		}
	}
	
	public static <R, I, D> ITransform<R, D> compose(final ITransform<R, I> t1, final ITransform<I, D> t2)
	{
		return new ITransform<R, D>()
		{
			@Override
			public D apply(R r)
			{
				return t2.apply(t1.apply(r));
			}
		};
	}
}
