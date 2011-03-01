package com.enokinomi.timeslice.web.prorata.client.ui.api;

import java.util.Map;

import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.google.gwt.user.client.ui.IsWidget;

public interface IProjectReportPanel extends IsWidget
{

    public static interface Listener
    {
        void assignPartialOrderingRequested(Map<String, Double> projectMap, int i, int j); // -> sendReorder

        void scaleToChanged(boolean isEnabled);

        void scaleToValueChanged(double scaleToValue);
    }


    Registration addListener(Listener listener);

    void setProjects(double total, Map<String, Double> projectMap);

    void setScalingEnabled(Boolean enabled, boolean fireEvents);

    void setScalingValue(Double value, boolean fireEvents);

}
