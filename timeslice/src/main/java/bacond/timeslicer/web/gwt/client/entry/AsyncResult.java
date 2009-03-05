package bacond.timeslicer.web.gwt.client.entry;

import org.restlet.gwt.data.Status;

public class AsyncResult<T>
{
	private final Status status;
	private final T returned;
	private final Throwable thrown;
	
	public boolean isError()
	{
		return !status.isError() && null != thrown;
	}
	
	public static AsyncResult<Void> returnedVoid(Status status)
	{
		return new AsyncResult<Void>(status, null, null);
	}
	
	public static <T> AsyncResult<T> returned(Status status, T returned)
	{
		return new AsyncResult<T>(status, returned, null);
	}
	
	public static <T> AsyncResult<T> threw(Status status, Throwable t)
	{
		return new AsyncResult<T>(status, null, t);
	}
	
	public AsyncResult(Status status, T returned, Throwable thrown)
	{
		this.status = status;
		this.returned = returned;
		this.thrown = thrown;
	}

	public Status getStatus()
	{
		return status;
	}

	public T getReturned()
	{
		return returned;
	}

	public Throwable getThrown()
	{
		return thrown;
	}
}