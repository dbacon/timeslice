package bacond.timeslice.web.gwt.client.server;

import bacond.timeslice.web.gwt.client.entry.AsyncResult;

public interface IRequestEnder<R>
{
	void end(AsyncResult<R> result);
}
