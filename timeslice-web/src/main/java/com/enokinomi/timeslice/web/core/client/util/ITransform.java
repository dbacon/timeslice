package com.enokinomi.timeslice.web.core.client.util;

public interface ITransform<R, D>
{
    D apply(R r);
}
