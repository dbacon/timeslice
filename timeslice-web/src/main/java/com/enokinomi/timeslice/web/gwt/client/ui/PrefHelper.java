package com.enokinomi.timeslice.web.gwt.client.ui;

import java.util.Date;

public class PrefHelper
{
    @SuppressWarnings("deprecation")
    public static Date createDateSufficientlyInTheFuture()
    {
        return new Date(2099, 0, 1);
    }
}
