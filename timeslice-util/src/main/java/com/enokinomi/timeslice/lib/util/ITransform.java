/**
 *
 */
package com.enokinomi.timeslice.lib.util;

public interface ITransform<R, D>
{
    D apply(R r);
}
