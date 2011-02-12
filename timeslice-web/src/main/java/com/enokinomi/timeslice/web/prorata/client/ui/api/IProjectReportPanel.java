package com.enokinomi.timeslice.web.prorata.client.ui.api;

import java.util.Map;

import com.enokinomi.timeslice.web.prorata.client.ui.impl.ProjectReportPanel.Listener;
import com.google.gwt.user.client.ui.IsWidget;

public interface IProjectReportPanel extends IsWidget
{

    void addListener(Listener listener);

    void setProjects(double total, Map<String, Double> projectMap);

}
