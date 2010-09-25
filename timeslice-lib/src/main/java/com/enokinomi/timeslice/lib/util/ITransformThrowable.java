package com.enokinomi.timeslice.lib.util;

public interface ITransformThrowable<R,D,T extends Exception>
{
    D apply(R r) throws T;
}
