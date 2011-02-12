package com.enokinomi.timeslice.web.prorata.client.ui.impl;

import com.google.gwt.i18n.client.Messages;

public interface ProjectReportPanelMessages extends Messages
{

    @DefaultMessage("{0,number,##0.000}")
    String direct(double value);

    @DefaultMessage("{0,number,##0.000}")
    String grandTotalScaled(double value);

}
