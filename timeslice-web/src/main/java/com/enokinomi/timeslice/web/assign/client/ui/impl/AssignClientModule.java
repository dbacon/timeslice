package com.enokinomi.timeslice.web.assign.client.ui.impl;

import com.enokinomi.timeslice.web.assign.client.ui.api.ITabularResultsAssignedView;
import com.google.gwt.inject.client.AbstractGinModule;

public class AssignClientModule extends AbstractGinModule
{

    @Override
    protected void configure()
    {
        bind(ITabularResultsAssignedView.class).to(TabularResultsAssignedView.class);
    }

}
