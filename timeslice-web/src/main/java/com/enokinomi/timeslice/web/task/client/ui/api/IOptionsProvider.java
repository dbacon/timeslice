package com.enokinomi.timeslice.web.task.client.ui.api;

public interface IOptionsProvider
{
    public static String CurrentTaskToken = "@current.task@";

    void addOptionsListener(IOptionsListener listener);
    void removeOptionsListener(IOptionsListener listener);

    int getMaxSize();
    long getMaxSeconds();
    boolean isControlSpaceSends();
    boolean isCurrentTaskInTitlebar();
    boolean isAutoRefresh();
    int getAutoRefreshMs();
    String getTitleBarTemplate();

}
