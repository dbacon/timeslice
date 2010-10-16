package com.enokinomi.timeslice.web.task.client.ui;

import com.google.gwt.i18n.client.Constants;

public interface ParamPanelConstants extends Constants
{

    @DefaultStringValue("For full-day: ")
    public String forFullDay();

    @DefaultStringValue("\u226b") // >>
    public String dateForward();

    @DefaultStringValue("\u226a") // <<
    public String dateReverse();

    @DefaultStringValue("Starting")
    public String starting();

    @DefaultStringValue("Ending")
    public String ending();

    @DefaultStringValue("Allow words containing:")
    public String allowWordsContaining();

    @DefaultStringValue("Ignore words containing:")
    public String ignoreWordsContaining();

}
