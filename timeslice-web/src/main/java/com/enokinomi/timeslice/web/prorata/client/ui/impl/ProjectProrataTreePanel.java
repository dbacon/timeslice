package com.enokinomi.timeslice.web.prorata.client.ui.impl;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.prorata.client.core.Group;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.prorata.client.presenter.impl.ProrataManagerPresenter;
import com.enokinomi.timeslice.web.prorata.client.ui.api.IProjectProrataTreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectProrataTreePanel extends Composite implements IProjectProrataTreePanel
{
    private static ProjectProrataTreePanelUiBinder uiBinder = GWT.create(ProjectProrataTreePanelUiBinder.class);
    interface ProjectProrataTreePanelUiBinder extends UiBinder<Widget, ProjectProrataTreePanel> { }

    private final ProjectProrataTreePanelMessages messages = GWT.create(ProjectProrataTreePanelMessages.class);
    private final ProjectProrataTreePanelConstants constants = GWT.create(ProjectProrataTreePanelConstants.class);

    @UiField protected FlexTable table;

    private final List<Listener> listeners = new ArrayList<Listener>();

    public static void bind(final IProjectProrataTreePanel ui, final IProrataManagerPresenter presenter)
    {
        presenter.addListener(new ProrataManagerPresenter.Listener()
        {
            @Override
            public void allGroupInfoChanged(List<Group> result)
            {
            }

            @Override
            public void addComplete()
            {
            }

            @Override
            public void removeComplete()
            {
            }

            @Override
            public void tasksUpdated()
            {
                ui.resetRows(presenter.getRows());
            }
        });

        ui.addListener(new IProjectProrataTreePanel.Listener()
        {
            @Override
            public void splitRequested(String project, String splitTo, Double weight)
            {
                presenter.addGroupComponent(project, splitTo, weight);
            }

            @Override
            public void deleteRequested(String parentName, String what)
            {
                presenter.removeGroupComponent(parentName, what);
            }
        });
    }

    protected void fireSplitRequested(String project, String splitTo, Double weight)
    {
        for (Listener listener: listeners)
        {
            listener.splitRequested(project, splitTo, weight);
        }
    }

    protected void fireDeleteRequested(String parentName, String what)
    {
        for(Listener listener: listeners)
        {
            listener.deleteRequested(parentName, what);
        }
    }

    @Override
    public void addListener(Listener listener)
    {
        if (null != listener)
        {
            listeners.add(listener);
        }
    }

    ProjectProrataTreePanel()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    private void addTableRow(double total, int rowIndex, final String what, double value, double weight, boolean isLeaf, final String parentName, int currentDepth, int[] siblingCounts, int[] siblingIndexes)
    {
        table.getRowFormatter().addStyleName(rowIndex, (rowIndex % 2) == 0 ? "evenRow" : "oddRow");

        Panel prefix = drawPrefix(siblingCounts, siblingIndexes);

        Label w = new Label(what);

        HorizontalPanel key = new HorizontalPanel();
        key.add(prefix);
        key.add(w);

        final Anchor splitLink = new Anchor("+");
        final Anchor deleteLink = new Anchor("-");

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(splitLink);
        Label space = new Label();
        space.setWidth("1em");
        hp.add(space);
        hp.add(deleteLink);

        splitLink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                new SplitProjectDialogBox(what, new SplitProjectDialogBox.Listener()
                {
                    @Override
                    public void added(String project, String splitTo, Double weight)
                    {
                        fireSplitRequested(project, splitTo, weight);
                    }
                }).showRelativeTo(splitLink);
            }
        });

        deleteLink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                fireDeleteRequested(parentName, what);
            }
        });

        deleteLink.setVisible(null != parentName);

        int col = 0;

        table.setWidget(rowIndex, col, key);
        table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        col++;

        table.setWidget(rowIndex, col, hp);
        table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        col++;

        table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(rowIndex, col, new Label(messages.direct(weight)));
        col++;

        table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        if (!isLeaf)
        {
            table.setWidget(rowIndex, col, new Label(messages.inherit(total)));
        }
        col++;

        table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        if (isLeaf)
        {
            table.setWidget(rowIndex, col, new Label(messages.direct(value)));
        }
        col++;
    }

    private Panel drawPrefix(int[] siblingCounts, int[] siblingIndexes)
    {
        HorizontalPanel p = new HorizontalPanel();

        int depth = siblingCounts.length;

        for (int i = 0; i < depth - 1; ++i)
        {
            int cnt = siblingCounts[i];
            int ind = siblingIndexes[i];

            boolean isLast = (ind + 1) == cnt;

            Image space = new Image();
            space.setStylePrimaryName("treeLine");
            Label bar = new Label("│");
            bar.setStylePrimaryName("treeLine");
            p.add(isLast ? space : bar);
        }

        if (depth > 1)
        {
            int cnt = siblingCounts[depth - 1];
            int ind = siblingIndexes[depth - 1];

            boolean isLast = (ind + 1) == cnt;

            Label mid = new Label("├");
            mid.setStylePrimaryName("treeLine");
            Label end = new Label("└");
            end.setStylePrimaryName("treeLine");

            p.add(isLast ? end : mid);
        }

        return p;
    }

    public static class Row
    {
        public String toString()
        {
            return what;
        }

        private final double total;
        private final int rowIndex;
        private final String what;
        private final double value;
        private final double weight;
        private final boolean isLeaf;
        private final String parentName;
        private final int currentDepth;
        private final int[] siblingCounts;
        private final int[] siblingIndexes;

        public Row(double total, int rowIndex, String what, double value,
                double weight, boolean isLeaf, String parentName,
                int currentDepth, int[] siblingCounts, int[] siblingIndexes)
        {
            this.total = total;
            this.rowIndex = rowIndex;
            this.what = what;
            this.value = value;
            this.weight = weight;
            this.isLeaf = isLeaf;
            this.parentName = parentName;
            this.currentDepth = currentDepth;
            this.siblingCounts = siblingCounts;
            this.siblingIndexes = siblingIndexes;
        }

        public double getTotal()
        {
            return total;
        }

        public int getRowIndex()
        {
            return rowIndex;
        }

        public String getWhat()
        {
            return what;
        }

        public double getValue()
        {
            return value;
        }

        public double getWeight()
        {
            return weight;
        }

        public boolean isLeaf()
        {
            return isLeaf;
        }

        public String getParentName()
        {
            return parentName;
        }

        public int getCurrentDepth()
        {
            return currentDepth;
        }

        public int[] getSiblingCounts()
        {
            return siblingCounts;
        }

        public int[] getSiblingIndexes()
        {
            return siblingIndexes;
        }
    }

    @Override
    public void resetRows(List<Row> rows)
    {
        clearAndInstallHeaders_table();
        for (Row row: rows)
        {
            addTableRow(
                    row.getTotal(),
                    row.getRowIndex(),
                    row.getWhat(),
                    row.getValue(),
                    row.getWeight(),
                    row.isLeaf(),
                    row.getParentName(),
                    row.getCurrentDepth(),
                    row.getSiblingCounts(),
                    row.getSiblingIndexes());
        }
    }

    private void clearAndInstallHeaders_table()
    {
        table.removeAllRows();
        int col = 0;

        table.getRowFormatter().setStylePrimaryName(0, "tsTableHeader");

        table.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(0, col, new HTML(constants.project()));
        ++col;

        table.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(0, col, new HTML("+/-"));
        ++col;

        table.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(0, col, new HTML(constants.weight()));
        ++col;

        table.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(0, col, new HTML(constants.inherited()));
        ++col;

        table.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(0, col, new HTML(constants.direct()));
        ++col;
    }

}
