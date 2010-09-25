package com.enokinomi.timeslice.web.gwt.client.task.ui;

import com.google.gwt.i18n.client.Constants;

public interface ReportPanelConstants extends Constants
{

    @DefaultStringValue("Refresh")
    String refresh();

    @DefaultStringValue("Persist")
    String persist();

    @DefaultStringValue("%D - selected full day;  %S - starting date/time;  %E - ending date/time")
    String persistedNameSubstitutionHint();

    @DefaultStringValue("Break-down")
    String totaling();

    @DefaultStringValue("Billing")
    String assigned();

}
