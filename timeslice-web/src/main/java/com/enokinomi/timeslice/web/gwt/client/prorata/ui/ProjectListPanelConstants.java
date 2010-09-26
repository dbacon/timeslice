package com.enokinomi.timeslice.web.gwt.client.prorata.ui;

import com.google.gwt.i18n.client.Constants;

public interface ProjectListPanelConstants extends Constants
{

    @DefaultStringValue("Project")
    String project();

    @DefaultStringValue("Direct")
    String direct();

    @DefaultStringValue("Inherit")
    String inherited();

    @DefaultStringValue("G.Total")
    String grandTotal();

    @DefaultStringValue("G.Total (Scaled)")
    String grandTotalScaled();

    @DefaultStringValue("Scale Totals")
    String scaleTotals();

}
