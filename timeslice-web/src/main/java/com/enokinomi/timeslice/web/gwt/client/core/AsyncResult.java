package com.enokinomi.timeslice.web.gwt.client.core;

public class AsyncResult<T>
{
    private final T returned;
    private final Throwable thrown;


    public boolean isError()
    {
        return null != thrown;
    }

    public static AsyncResult<Void> returnedVoid()
    {
        return new AsyncResult<Void>(null, null);
    }

    public static <T> AsyncResult<T> returned(T returned)
    {
        return new AsyncResult<T>(returned, null);
    }

    public static <T> AsyncResult<T> threw(Throwable t)
    {
        return new AsyncResult<T>(null, t);
    }

    public AsyncResult(T returned, Throwable thrown)
    {
        this.returned = returned;
        this.thrown = thrown;
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
