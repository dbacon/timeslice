package com.enokinomi.timeslice.web.util;

import java.util.Collection;

import com.enokinomi.timeslice.lib.util.ITransform;


public class Transform
{
    Transform()
    {
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
