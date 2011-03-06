package com.enokinomi.timeslice.web.core.server.util;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.enokinomi.timeslice.web.core.client.util.ServiceException;

public class Catcher
{
    private final static Logger log = Logger.getLogger(Catcher.class);

    public Catcher()
    {
    }

    public <T> T catchAndWrap(String wrapperDescriptionOnFailure, Callable<T> code) throws ServiceException
    {
        try
        {
            return code.call();
        }
        catch (Exception e)
        {
            if (e instanceof ServiceException)
            {
                throw (ServiceException) e;
            }
            else if (e instanceof NullPointerException)
            {
                log.error("null-pointer detected", e);

                throw new RuntimeException("null-pointer re-throw", e);
            }
            else
            {
                throw new ServiceException(wrapperDescriptionOnFailure + ": call failed, exception type '" + e.getClass().getCanonicalName() + "': " + e.getMessage(), e);
            }
        }
    }
}
