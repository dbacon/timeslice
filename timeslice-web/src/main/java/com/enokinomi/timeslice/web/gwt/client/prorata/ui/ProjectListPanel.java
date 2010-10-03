package com.enokinomi.timeslice.web.gwt.client.prorata.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.enokinomi.timeslice.web.gwt.client.assigned.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.controller.IAuthTokenHolder;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.Group;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.GroupComponent;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvc;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvcAsync;
import com.enokinomi.timeslice.web.gwt.client.prorata.tree.Branch;
import com.enokinomi.timeslice.web.gwt.client.prorata.tree.IVisitor;
import com.enokinomi.timeslice.web.gwt.client.prorata.tree.Leaf;
import com.enokinomi.timeslice.web.gwt.client.prorata.tree.LeafOnlyTotalingVisitor;
import com.enokinomi.timeslice.web.gwt.client.prorata.tree.MapRuleSource;
import com.enokinomi.timeslice.web.gwt.client.prorata.tree.TotalTotalingVisitor;
import com.enokinomi.timeslice.web.gwt.client.prorata.tree.Tree;
import com.enokinomi.timeslice.web.gwt.client.prorata.ui.ProRataManagerPanel.Listener;
import com.enokinomi.timeslice.web.gwt.client.ui.PrefHelper;
import com.enokinomi.timeslice.web.gwt.client.util.Checks;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ProjectListPanel extends Composite
{
    private final ProjectListPanelConstants constants = GWT.create(ProjectListPanelConstants.class);
    private final ProjectListPanelMessages messages = GWT.create(ProjectListPanelMessages.class);

    public static class PrefKeys
    {
        public static final String Scale = "timeslice.project.scale";
        public static final String ScaleTo = "timeslice.project.scaleto";
    }

    private final FlexTable table = new FlexTable();
    private final FlexTable projectTable = new FlexTable();

    private final CheckBox scaleCheckBox = new CheckBox(constants.scaleTotals());
    private final TextBox scaleToTextBox = new TextBox();
    private final ProRataManagerPanel proRataManagePanel = new ProRataManagerPanel();

    private final IProRataSvcAsync prorataSvc = GWT.create(IProRataSvc.class);

    private IAuthTokenHolder tokenHolder;

    Map<String, Tree> results = new LinkedHashMap<String, Tree>();

    private List<AssignedTaskTotal> itemsCache = new ArrayList<AssignedTaskTotal>();

    public ProjectListPanel()
    {
        table.setStylePrimaryName("tsMathTable");
        projectTable.setStylePrimaryName("tsMathTable");


        scaleCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event)
            {
                scaleToTextBox.setEnabled(scaleCheckBox.getValue());
                scaleToTextBox.setVisible(scaleCheckBox.getValue());

                writePrefs();
            }
        });

        scaleToTextBox.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(ChangeEvent event)
            {
                writePrefs();
            }
        });

        scaleToTextBox.setWidth("3em");

        readPrefs();

        scaleToTextBox.setEnabled(scaleCheckBox.getValue());
        scaleToTextBox.setVisible(scaleCheckBox.getValue());

        HorizontalPanel hp1 = new HorizontalPanel();
        hp1.add(scaleCheckBox);
        hp1.add(scaleToTextBox);

        proRataManagePanel.addListener(new Listener()
        {
            @Override
            public void groupsChanged()
            {
                update(null);
            }
        });


        TabLayoutPanel visualizerTabs = new TabLayoutPanel(2, Unit.EM);
        visualizerTabs.add(new ScrollPanel(table), constants.projectBreakdown());
        visualizerTabs.add(new ScrollPanel(proRataManagePanel), constants.allRules());

        DockLayoutPanel dp = new DockLayoutPanel(Unit.EM);
        dp.addNorth(hp1, 4);
        dp.add(visualizerTabs);

        TabLayoutPanel tabs = new TabLayoutPanel(2, Unit.EM);
        tabs.add(new ScrollPanel(projectTable), constants.report());
        tabs.add(dp, constants.proRataMaintenance());

        initWidget(tabs);

        clearAndInstallHeaders();
    }

    private void clearAndInstallHeaders()
    {
        clearAndInstallHeaders_table();
        clearAndInstallHeaders_projectTable();
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

    private void clearAndInstallHeaders_projectTable()
    {
        projectTable.removeAllRows();
        int col = 0;

        projectTable.getRowFormatter().setStylePrimaryName(0, "tsTableHeader");

        projectTable.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        projectTable.setWidget(0, col, new HTML(constants.project()));
        ++col;

        projectTable.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        projectTable.setWidget(0, col, new HTML(constants.total()));
        ++col;

        if (scaleCheckBox.getValue())
        {
            projectTable.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            projectTable.setWidget(0, col, new HTML(constants.scaled()));
        }

        ++col;
    }

    public void setAuthTokenHolder(IAuthTokenHolder tokenHolder)
    {
        this.tokenHolder = tokenHolder;

        proRataManagePanel.setAuthTokenHolder(tokenHolder);
    }

    public void update(List<AssignedTaskTotal> items)
    {
        proRataManagePanel.refresh();

        results.clear();

        if (items != null)
        {
            itemsCache = items;
        }

        for (AssignedTaskTotal item: itemsCache)
        {
            Leaf leaf = (Leaf) Checks.mapNullTo(results.get(item.getBilledTo()), new Leaf(item.getBilledTo(), 1., 0.));
            Leaf newLeaf = new Leaf(leaf.getName(), leaf.getWeight(), leaf.getValue() + item.getHours());
            results.put(newLeaf.getName(), newLeaf);
        }

        redraw();
        requestExpansion();
    }

    public void redraw()
    {
        redrawTable();
        redrawGraph();
    }

    private void redrawGraph()
    {
        // TODO: draw a graph of the group structure
    }

    private void requestExpansion()
    {
        String authToken = (null == tokenHolder) ? null : tokenHolder.getAuthToken();

        if (null == authToken)
        {
            GWT.log("No authorization token available - can't expand groups.");
            return;
        }

        // request rule map for expansion.
        prorataSvc.listAllGroupInfo(authToken, new AsyncCallback<List<Group>>()
        {
            @Override
            public void onSuccess(List<Group> result)
            {
                MapRuleSource ruleSource = new MapRuleSource();
                for (Group group: result)
                {
                    ruleSource.add(group.getName(), group.getComponents().toArray(new GroupComponent[group.getComponents().size()]));
                }

                List<Tree> expandedResults = new ArrayList<Tree>();

                for (Tree root: results.values())
                {
                     expandedResults.add(root.expand(ruleSource, 0.0001));
                }

                results.clear();
                for (Tree tree: expandedResults)
                {
                    results.put(tree.getName(), tree);
                }

                redraw();
            }

            @Override
            public void onFailure(Throwable caught)
            {
                GWT.log("Listing all group-info failed: " + caught.getMessage(), caught);
            }
        });
    }

    class RowMaker implements IVisitor<RowMaker>
    {
        private final double total;
        private int rowIndex;

        public RowMaker(double total, int rowIndex)
        {
            this.total = total;
            this.rowIndex = rowIndex;
        }

        @Override
        public RowMaker visit(Tree t, Branch parent, int currentDepth, int[] siblingCounts, int[] siblingIndexs)
        {
            treeToRow(total, rowIndex, t, parent, currentDepth, siblingCounts, siblingIndexs);
            rowIndex++;
            return this;
        }
    };


    private void redrawTable()
    {
        clearAndInstallHeaders();

        double total = 0.;
        TotalTotalingVisitor totaler = new TotalTotalingVisitor();
        for (Tree row: results.values())
        {
            row.accept(totaler);
        }

        Map<String, Double> totals = totaler.getTotals();

        for (Double p: totals.values()) total += p;

        RowMaker rowMaker = new RowMaker(total, 1);
        for (Tree row: results.values())
        {
            row.accept(rowMaker);
        }


        LeafOnlyTotalingVisitor leafTotaler = new LeafOnlyTotalingVisitor();
        for (Tree row: results.values())
        {
            row.accept(leafTotaler);
        }

        int rowi = 1;
        Set<Entry<String, Double>> leafTotals = leafTotaler.getTotals().entrySet();
        for (Entry<String, Double> p: leafTotals)
        {
            int coli = 0;

            projectTable.getRowFormatter().addStyleName(rowi, (rowi % 2) == 0 ? "evenRow" : "oddRow");

            projectTable.getCellFormatter().setAlignment(rowi, coli, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
            projectTable.setWidget(rowi, coli++, new Label(p.getKey()));

            projectTable.getCellFormatter().setAlignment(rowi, coli, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            projectTable.setWidget(rowi, coli++, new Label(messages.direct(p.getValue())));

            if (scaleCheckBox.getValue())
            {
                Double target = Double.valueOf(scaleToTextBox.getText());
                projectTable.setWidget(rowi, coli, new Label(messages.grandTotalScaled(p.getValue() / total * target)));
                projectTable.getCellFormatter().setAlignment(rowi, coli, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            }

            coli++;

            ++rowi;
        }
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

    private void treeToRow(double total, int rowIndex, final Tree row, final Branch parent, int currentDepth, int[] siblingCounts, int[] siblingIndexes)
    {
        table.getRowFormatter().addStyleName(rowIndex, (rowIndex % 2) == 0 ? "evenRow" : "oddRow");

        Panel prefix = drawPrefix(siblingCounts, siblingIndexes);

        Label w = new Label(row.getName());

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
                new SplitProjectDialogBox(row.getName(), new SplitProjectDialogBox.Listener()
                {
                    @Override
                    public void added(String project, String splitTo, Double weight)
                    {
                        // TODO: disallow non-devolving cycles (insid the svc would be best?)
                        prorataSvc.addGroupComponent(tokenHolder.getAuthToken(), project, splitTo, weight, new AsyncCallback<Void>()
                            {
                                @Override
                                public void onFailure(Throwable caught)
                                {
                                    GWT.log("Failure adding new group: " + caught.getMessage());
                                }

                                @Override
                                public void onSuccess(Void result)
                                {
                                    update(null);
                                }
                            });
                    }
                }).showRelativeTo(splitLink);
            }
        });

        deleteLink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                prorataSvc.removeGroupComponent(tokenHolder.getAuthToken(), parent.getName(), row.getName(), new AsyncCallback<Void>()
                {
                    @Override
                    public void onFailure(Throwable caught)
                    {
                        GWT.log("Failure removing group: " + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Void result)
                    {
                        update(null);
                    }
                });
            }
        });

        deleteLink.setVisible(null != parent);

        int col = 0;

        table.setWidget(rowIndex, col, key);
        table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        col++;

        table.setWidget(rowIndex, col, hp);
        table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        col++;

        table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        table.setWidget(rowIndex, col, new Label(messages.direct(row.getWeight())));
        col++;

        table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        if (!row.isLeaf())
        {
            table.setWidget(rowIndex, col, new Label(messages.inherit(row.getTotal())));
        }
        col++;

        table.getCellFormatter().setAlignment(rowIndex, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        if (row.isLeaf())
        {
            table.setWidget(rowIndex, col, new Label(messages.direct(row.getValue())));
        }
        col++;
    }

    private void readPrefs()
    {
        scaleCheckBox.setValue("true".equals(Cookies.getCookie(PrefKeys.Scale)));
        scaleToTextBox.setText(Cookies.getCookie(PrefKeys.ScaleTo));
    }

    private void writePrefs()
    {
        Cookies.setCookie(PrefKeys.Scale, scaleCheckBox.getValue() ? "true" : "false", PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.ScaleTo, scaleToTextBox.getValue(), PrefHelper.createDateSufficientlyInTheFuture());
    }
}
