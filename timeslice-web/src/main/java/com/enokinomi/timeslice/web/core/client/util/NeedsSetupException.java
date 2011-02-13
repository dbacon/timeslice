package com.enokinomi.timeslice.web.core.client.util;

public class NeedsSetupException extends ServiceException
{
    private static final long serialVersionUID = 1L;

    public NeedsSetupException()
    {
        super();
    }

    public NeedsSetupException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NeedsSetupException(String message)
    {
        super(message);
    }

    public NeedsSetupException(Throwable cause)
    {
        super(cause);
    }

}
