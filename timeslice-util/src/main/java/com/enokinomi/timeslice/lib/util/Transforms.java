package com.enokinomi.timeslice.lib.util;

import java.util.Collection;

public class Transforms
{
    public static <C, M> ITransform<C, M> member(final Class<C> classClass, final Class<M> memberClass, final String memberName)
    {
        return new ITransform<C, M>()
        {
            @Override
            public M apply(C r)
            {
                String methodName = "get" + memberName.substring(0, 1).toUpperCase() + memberName.substring(1);
                try
                {
                    return Narrow.<M>castSingle(r.getClass().getMethod(methodName, new Class[0]).invoke(r, new Object[0]));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(
                            "Could not retrieve member " +
                            "'" + classClass.getName() + "'" +
                            " on class " +
                            "'" + classClass.getName() + "'" +
                            ": " + e.getMessage(), e);
                }
            }
        };
    }

    public static <T> ITransform<T, T> identity()
    {
        return new ITransform<T, T>()
        {
            @Override
            public T apply(T r)
            {
                return r;
            }
        };
    }

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

    public static <R, CT extends Collection<D>, D> CT tr(Collection<R> xs, CT result, ITransform<R, D> f)
    {
        // no ordering, could be parallelized.
        for (R x: xs)
        {
            result.add(f.apply(x));
        }

        return result;
    }

}
