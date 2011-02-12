package com.enokinomi.timeslice.web.prorata.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.google.gwt.user.client.ui.IsWidget;

public interface IProjectListPanel extends IsWidget
{

    void clear();

    void update(List<AssignedTaskTotal> report);

}
