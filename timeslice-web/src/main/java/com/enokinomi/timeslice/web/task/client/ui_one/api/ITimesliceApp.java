package com.enokinomi.timeslice.web.task.client.ui_one.api;

import com.enokinomi.timeslice.web.task.client.ui.api.IIsWidget;

public interface ITimesliceApp extends IIsWidget
{
    public static final class Defaults
    {
        public static final int MaxResults = 10;
        public static final long MaxSeconds = 60 * 60 * 24;
        public static final int AutoRefreshMs = 500;
    }

}
