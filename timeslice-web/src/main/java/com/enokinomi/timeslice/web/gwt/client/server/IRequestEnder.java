package com.enokinomi.timeslice.web.gwt.client.server;

import com.enokinomi.timeslice.web.gwt.client.entry.AsyncResult;

public interface IRequestEnder<R>
{
    void end(AsyncResult<R> result);
}
