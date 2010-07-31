/**
 *
 */
package com.enokinomi.timeslice.web.gwt.client.widget;

public interface IOptionsProvider
{
    public static String CurrentTaskToken = "@current.task@";

    int getMaxSize();
    long getMaxSeconds();
    boolean isControlSpaceSends();
    boolean isCurrentTaskInTitlebar();
    boolean isAutoRefresh();
    int getAutoRefreshMs();
    String getTitleBarTemplate();

}
