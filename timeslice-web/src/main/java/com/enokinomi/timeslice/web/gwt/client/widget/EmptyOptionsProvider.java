/**
 *
 */
package com.enokinomi.timeslice.web.gwt.client.widget;

public class EmptyOptionsProvider implements IOptionsProvider
{
    @Override
    public int getMaxSize()
    {
        return 50;
    }

    @Override
    public long getMaxSeconds()
    {
        return 60*60*24;
    }

    @Override
    public String getTitleBarTemplate()
    {
        return "Timeslice";
    }

    @Override
    public boolean isControlSpaceSends()
    {
        return false;
    }

    @Override
    public boolean isCurrentTaskInTitlebar()
    {
        return false;
    }

    @Override
    public boolean isAutoRefresh()
    {
        return false;
    }

    @Override
    public int getAutoRefreshMs()
    {
        return Integer.MAX_VALUE;
    }

}
