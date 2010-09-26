package com.enokinomi.timeslice.web.gwt.client.util;

import java.util.Collection;


public class TransformUtils
{
    public static <A,B,C> Tx<A,C> comp(final Tx<A,B> f, final Tx<B,C> g)
    {
        return new Tx<A, C>()
        {
            @Override
            public C apply(A r)
            {
                return g.apply(f.apply(r));
            }
        };
    }

    public static <T> Tx<T,T> identity()
    {
        return new Tx<T,T>()
        {
            @Override
            public T apply(T r)
            {
                return r;
            }
        };
    }

    public static <T> Tx<T,String> stringify()
    {
        return new Tx<T, String>()
        {
            @Override
            public String apply(T r)
            {
                return r.toString();
            }
        };
    }

    public static <R, CT extends Collection<D>, D> CT tr(Collection<R> xs, CT result, ITransform<R, D> f)
    {
        for (R x: xs)
        {
            result.add(f.apply(x));
        }

        return result;
    }

}
