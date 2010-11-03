package com.enokinomi.timeslice.web.task.client.ui.impl;

import com.google.gwt.i18n.client.Constants;

public interface OptionsConstants extends Constants
{
    @DefaultStringValue("Control-space also sends.")
    public String controlSpaceAlsoSends();

    @DefaultStringValue("Show current task in page title.")
    public String showCurrentTaskInPageTitle();

    @DefaultStringValue("Auto-refresh")
    public String autoRefresh();

    @DefaultStringValue("Max results")
    public String maxResults();

    @DefaultStringValue("Max hours")
    public String maxHours();

    @DefaultStringValue("Number of items to show in history and include in word-completion.")
    public String maxResultsHint();

    @DefaultStringValue("Number of hours (decimal ok) to show in history and include in word-completion.")
    public String maxHoursHint();

}
