package com.enokinomi.timeslice.web.gwt.client.core;

public class NotAuthenticException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public NotAuthenticException()
    {
        super();
    }

    public NotAuthenticException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NotAuthenticException(String message)
    {
        super(message);
    }

    public NotAuthenticException(Throwable cause)
    {
        super(cause);
    }

}