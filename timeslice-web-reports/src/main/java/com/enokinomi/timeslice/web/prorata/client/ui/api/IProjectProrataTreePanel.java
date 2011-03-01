package com.enokinomi.timeslice.web.prorata.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.prorata.client.ui.impl.ProjectProrataTreePanel.Row;
import com.google.gwt.user.client.ui.IsWidget;

public interface IProjectProrataTreePanel extends IsWidget
{
    public interface Listener
    {
        void splitRequested(String project, String splitTo, Double weight);

        void deleteRequested(String parentName, String what);
    }

    Registration addListener(Listener listener);

    void resetRows(List<Row> rows);

}
