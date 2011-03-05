package com.enokinomi.timeslice.web.core.server.util;

import java.util.concurrent.Callable;

import com.enokinomi.timeslice.web.core.client.util.ServiceException;

public class Catcher
{
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
            else
            {
                throw new ServiceException(wrapperDescriptionOnFailure + ": call failed: " + e.getMessage(), e);
            }
        }
    }
}
