package bacond.timeslicer.web.gwt.client.server;

import bacond.timeslicer.web.gwt.client.entry.AsyncResult;

public interface IRequestEnder<R>
{
	void end(AsyncResult<R> result);
}