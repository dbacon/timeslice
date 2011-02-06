package com.enokinomi.timeslice.web.task.client.ui_one.api;

import com.google.gwt.user.client.ui.IsWidget;

public interface ITimesliceApp extends IsWidget
{
    public static final class Defaults
    {
        public static final int MaxResults = 10;
        public static final long MaxSeconds = 60 * 60 * 24;
        public static final int AutoRefreshMs = 500;
    }

}
