package com.enokinomi.timeslice.web.prorata.client.presenter.api;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectProrataTreePanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectReportPanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProrataManagerPanel;

public interface IProrataManagerPresenter
{

    // TODO: this is going away - for now allow another service to tell us some data.
    void setStuff(List<AssignedTaskTotal> report);

    void bind(IProjectReportPanel projectReportPanel);

    void bind(IProrataManagerPanel prorataManagePanel);

    void bind(IProjectProrataTreePanel projectProrataTreePanel);

}
