package com.enokinomi.timeslice.web.prorata.client.ui.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectListPanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectProrataTreePanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectReportPanel;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProrataManagerPanel;
import com.enokinomi.timeslice.web.prorata.client.ui.impl.ProjectProrataTreePanel.Row;
import com.enokinomi.timeslice.web.settings.client.presenter.api.ISettingsPresenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ProjectListPanel extends Composite implements IProjectListPanel
{
    private static ProjectListPanelUiBinder uiBinder = GWT.create(ProjectListPanelUiBinder.class);
    interface ProjectListPanelUiBinder extends UiBinder<Widget, ProjectListPanel> { }

    @UiField protected IProrataManagerPanel prorataManagePanel;
    @UiField protected IProjectProrataTreePanel projectProrataTreePanel;
    @UiField protected IProjectReportPanel projectReportPanel;

    @Inject
    ProjectListPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public List<Registration> bind(IProrataManagerPresenter prorataManagerPresenter, ISettingsPresenter settingsPresenter)
    {
        List<Registration> results = new ArrayList<Registration>();
        results.addAll(ProjectReportPanel.bind(projectReportPanel, prorataManagerPresenter, settingsPresenter));
        results.addAll(ProrataManagerPanel.bind(prorataManagePanel, prorataManagerPresenter));
        results.addAll(ProjectProrataTreePanel.bind(projectProrataTreePanel, prorataManagerPresenter));
        return results;
    }

    public void clear()
    {
        projectProrataTreePanel.resetRows(Collections.<Row>emptyList());
        prorataManagePanel.clear();
        projectReportPanel.setProjects(0, new LinkedHashMap<String, Double>());
    }

}
