package com.enokinomi.timeslice.web.task.server.impl;

import java.util.concurrent.Callable;

import com.enokinomi.timeslice.web.core.client.util.ServiceException;

public class Catcher
{
    public <T> T catchAndWrap(String msg, Callable<T> code) throws ServiceException
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
                throw new ServiceException(msg + ": call failed: " + e.getMessage(), e);
            }
        }
    }
}
