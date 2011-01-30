package com.enokinomi.timeslice.lib.util;

public class WrapCheckedExceptionTransform<R,D> implements ITransform<R,D>
{
    private final ITransformThrowable<R, D, Exception> tx;

    public WrapCheckedExceptionTransform(ITransformThrowable<R, D, Exception> tx)
    {
        this.tx = tx;
    }

    public static <R, D> WrapCheckedExceptionTransform<R, D> create(ITransformThrowable<R, D, Exception> tx)
    {
        return new WrapCheckedExceptionTransform<R, D>(tx);
    }

    @Override
    public D apply(R r)
    {
        try
        {
            return tx.apply(r);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Wrapping checked-exception: " + e.getMessage(), e);
        }
    }
}
