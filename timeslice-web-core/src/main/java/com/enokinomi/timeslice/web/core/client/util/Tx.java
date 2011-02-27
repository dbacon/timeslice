/**
 *
 */
package com.enokinomi.timeslice.web.core.client.util;

public interface Tx<R,D>
{
    D apply(R r);
}
