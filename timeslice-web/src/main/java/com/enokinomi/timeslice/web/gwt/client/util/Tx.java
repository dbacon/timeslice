/**
 *
 */
package com.enokinomi.timeslice.web.gwt.client.util;

public interface Tx<R,D>
{
    D apply(R r);
}
