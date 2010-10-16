package com.enokinomi.timeslice.web.core.client.util;

public class Checks
{
    public static <T> T mapNullTo(T t, T altT)
    {
        if (null == t)
        {
            return altT;
        }
        else
        {
            return t;
        }
    }

}
