package com.enokinomi.timeslice.web.prorata.client.ui.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectListPanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectProrataTreePanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectReportPanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProrataManagerPanel;
import com.enokinomi.timeslice.web.prorata.client.ui.impl.ProjectProrataTreePanel.Row;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.inject.Inject;

public class ProjectListPanel extends Composite implements IProjectListPanel
{
    private final ProjectListPanelConstants constants;
    private final IProrataManagerPanel prorataManagePanel;

    private final IProrataManagerPresenter prorataManagerPresenter;
    private final IProjectProrataTreePanel projectProrataTreePanel;
    private final IProjectReportPanel projectReportPanel;

    @Inject
    ProjectListPanel(
            ProjectListPanelConstants constants,
            IProrataManagerPresenter prorataManagerPresenter,
            IProjectReportPanel projectReportPanel,
            IProjectProrataTreePanel projectProrataTreePanel,
            IProrataManagerPanel prorataManagePanel)
    {
        this.constants = constants;
        this.prorataManagerPresenter = prorataManagerPresenter;
        this.projectReportPanel = projectReportPanel;
        this.projectProrataTreePanel = projectProrataTreePanel;
        this.prorataManagePanel = prorataManagePanel;

        this.prorataManagerPresenter.bind(this.projectReportPanel);
        this.prorataManagerPresenter.bind(this.prorataManagePanel);
        this.prorataManagerPresenter.bind(this.projectProrataTreePanel);

        TabLayoutPanel visualizerTabs = new TabLayoutPanel(2, Unit.EM);
        visualizerTabs.add(new ScrollPanel(this.projectProrataTreePanel.asWidget()), this.constants.projectBreakdown());
        visualizerTabs.add(new ScrollPanel(this.prorataManagePanel.asWidget()), this.constants.allRules());

        TabLayoutPanel tabs = new TabLayoutPanel(2, Unit.EM);
        tabs.add(new ScrollPanel(this.projectReportPanel.asWidget()), this.constants.report());
        tabs.add(visualizerTabs, this.constants.proRataMaintenance());

        initWidget(tabs);
    }

    public void clear()
    {
        projectProrataTreePanel.resetRows(Collections.<Row>emptyList());
        prorataManagePanel.clear();
        projectReportPanel.setProjects(0, new LinkedHashMap<String, Double>());
    }

    // TODO: temp get rid of it.
    public void update(List<AssignedTaskTotal> report)
    {
        prorataManagerPresenter.setStuff(report);
    }

}
