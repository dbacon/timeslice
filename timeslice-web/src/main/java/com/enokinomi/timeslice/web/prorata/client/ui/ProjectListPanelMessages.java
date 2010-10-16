package com.enokinomi.timeslice.web.prorata.client.ui;

import com.google.gwt.i18n.client.Messages;

public interface ProjectListPanelMessages extends Messages
{
    @DefaultMessage("{0,number,##0.000}")
    String direct(double value);

    @DefaultMessage("{0,number,##0.000}")
    String inherit(double value);

    @DefaultMessage("{0,number,##0.000}")
    String grandTotal(double value);

    @DefaultMessage("{0,number,##0.000}")
    String grandTotalScaled(double value);

}
