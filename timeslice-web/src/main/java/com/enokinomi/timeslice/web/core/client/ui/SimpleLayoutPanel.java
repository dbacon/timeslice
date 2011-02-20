package com.enokinomi.timeslice.web.core.client.ui;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;

public class SimpleLayoutPanel extends LayoutPanel implements AcceptsOneWidget
{
    @Override
    public void setWidget(IsWidget w)
    {
        clear();
        if (w != null) this.add(w);
    }
}
