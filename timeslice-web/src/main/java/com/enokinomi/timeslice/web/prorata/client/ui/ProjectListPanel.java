package com.enokinomi.timeslice.web.prorata.client.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.util.Checks;
import com.enokinomi.timeslice.web.ordering.client.core.IOrderingSvc;
import com.enokinomi.timeslice.web.ordering.client.core.IOrderingSvcAsync;
import com.enokinomi.timeslice.web.prorata.client.core.Group;
import com.enokinomi.timeslice.web.prorata.client.core.GroupComponent;
import com.enokinomi.timeslice.web.prorata.client.core.IProRataSvc;
import com.enokinomi.timeslice.web.prorata.client.core.IProRataSvcAsync;
import com.enokinomi.timeslice.web.prorata.client.tree.Branch;
import com.enokinomi.timeslice.web.prorata.client.tree.IVisitor;
import com.enokinomi.timeslice.web.prorata.client.tree.Leaf;
import com.enokinomi.timeslice.web.prorata.client.tree.LeafOnlyTotalingVisitor;
import com.enokinomi.timeslice.web.prorata.client.tree.MapRuleSource;
import com.enokinomi.timeslice.web.prorata.client.tree.Tree;
import com.enokinomi.timeslice.web.prorata.client.ui.ProRataManagerPanel.Listener;
import com.enokinomi.timeslice.web.task.client.controller.IAuthTokenHolder;
import com.enokinomi.timeslice.web.task.client.ui_one.PrefHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

public class ProjectListPanel extends Composite
{
    private static final String ORDERING__TS_PROJECTS = "ts-projects";
    private final ProjectListPanelConstants constants = GWT.create(ProjectListPanelConstants.class);
    private final ProjectListPanelMessages messages = GWT.create(ProjectListPanelMessages.class);

    public static class PrefKeys
    {
        public static final String Scale = "timeslice.project.scale";
        public static final String ScaleTo = "timeslice.project.scaleto";
        public static final String AutoOrder = "timeslice.project.autoorder";
    }

    private final FlexTable table = new FlexTable();
    private final FlexTable projectTable = new FlexTable();

    private final CheckBox scaleCheckBox = new CheckBox(constants.scaleTotals());
    private final TextBox scaleToTextBox = new TextBox();
    private final CheckBox orderingCheckBox = new CheckBox("Auto-apply Ordering");
    private final Button orderButton = new Button("Order now");
    private final ProRataManagerPanel proRataManagePanel;

    private final IProRataSvcAsync prorataSvc = GWT.create(IProRataSvc.class);
    private final IOrderingSvcAsync orderingSvc = GWT.create(IOrderingSvc.class);

    private final IAuthTokenHolder tokenHolder;

    private int calcDelay = 1;

    Map<String, Tree> results = new LinkedHashMap<String, Tree>();

    private List<AssignedTaskTotal> itemsCache = new ArrayList<AssignedTaskTotal>();

    @Inject
    public ProjectListPanel(IAuthTokenHolder tokenHolder, ProRataManagerPanel proRataManagePanel)
    {
        this.tokenHolder = tokenHolder;
        this.proRataManagePanel = proRataManagePanel;

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

                onResultsExpanded(); // shouldn't be using this -- abuse.
            }
        });

        scaleToTextBox.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(ChangeEvent event)
            {
                writePrefs();

                onResultsExpanded(); // shouldn't be using this -- abuse.
            }
        });

        scaleToTextBox.setWidth("3em");

        readPrefs();

        scaleToTextBox.setEnabled(scaleCheckBox.getValue());
        scaleToTextBox.setVisible(scaleCheckBox.getValue());

        HorizontalPanel hp1 = new HorizontalPanel();
        hp1.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hp1.add(scaleCheckBox);
        hp1.add(scaleToTextBox);

        orderingCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event)
            {
                orderButton.setEnabled(!orderingCheckBox.getValue());

                writePrefs();

                if (event.getValue())
                {
                    requestOrdering(grandTotal, leafTotals);
                }
            }
        });
        orderButton.setEnabled(!orderingCheckBox.getValue());


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

        orderButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                requestOrdering(grandTotal, leafTotals);
            }
        });

        HorizontalPanel hp2 = new HorizontalPanel();
        hp2.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hp2.add(orderingCheckBox);
        hp2.add(orderButton);

        VerticalPanel settingsVp = new VerticalPanel();
        settingsVp.add(hp1);
        settingsVp.add(hp2);

        DisclosurePanel settingsPanel = new DisclosurePanel("Table settings");
        settingsPanel.add(settingsVp);

        VerticalPanel vp = new VerticalPanel();
        vp.add(new ScrollPanel(projectTable));
        vp.add(settingsPanel);


        TabLayoutPanel tabs = new TabLayoutPanel(2, Unit.EM);
        tabs.add(new ScrollPanel(vp), constants.report());
        tabs.add(visualizerTabs, constants.proRataMaintenance());

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
        projectTable.setWidget(0, col, new HTML(constants.moveDown()));
        ++col;

        projectTable.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        projectTable.setWidget(0, col, new HTML(constants.moveUp()));
        ++col;

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

    public void update(List<AssignedTaskTotal> items)
    {
        proRataManagePanel.refresh();

        results.clear();
        leafTotals.clear();

        if (items != null)
        {
            itemsCache = items;
        }

        grandTotal = 0.;

        for (AssignedTaskTotal item: itemsCache)
        {
            Leaf leaf = (Leaf) Checks.mapNullTo(results.get(item.getBilledTo()), new Leaf(item.getBilledTo(), 1., 0.));
            Leaf newLeaf = new Leaf(leaf.getName(), leaf.getWeight(), leaf.getValue() + item.getHours());
            results.put(newLeaf.getName(), newLeaf);

            grandTotal += newLeaf.getTotal();

            leafTotals.put(newLeaf.getName(), newLeaf.getValue());
        }

        onResultsRefreshed();
    }

    //  results -+-> expand -+-> leaf-total --> draw-projects
    //           '-> draw    +-> draw

    protected void onResultsRefreshed()
    {
        redrawTable();
        drawProjects(grandTotal, leafTotals);

        new Timer()
        {
            @Override
            public void run()
            {
                requestExpansion();
            }
        }.schedule(calcDelay);
    }

    protected void onResultsExpanded()
    {
        redrawTable();
        drawProjects(grandTotal, leafTotals);

        scheduleCalcLeafTotals();
    }

    private void scheduleCalcLeafTotals()
    {
        new Timer()
        {
            @Override
            public void run()
            {
                calcLeafTotals();
            }
        }.schedule(calcDelay);
    }

    protected void onLeafTotalsChanged()
    {
        drawProjects(grandTotal, leafTotals);

        if (orderingCheckBox.getValue())
        {
            new Timer()
            {
                @Override
                public void run()
                {
                    requestOrdering(grandTotal, leafTotals);
                }
            }.schedule(calcDelay);
        }
    }

    protected void onLeafTotalsOrdered()
    {
        drawProjects(grandTotal, leafTotals);
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
                leafTotals.clear();
                grandTotal = 0.;
                for (Tree tree: expandedResults)
                {
                    grandTotal += tree.getTotal();
                    results.put(tree.getName(), tree);
                    leafTotals.put(tree.getName(), tree.getTotal());
                }

                onResultsExpanded();
            }

            @Override
            public void onFailure(Throwable caught)
            {
                GWT.log("Listing all group-info failed: " + caught.getMessage(), caught);
            }
        });
    }

    private void requestOrdering(final double total, final Map<String, Double> map)
    {
        String authToken = (null == tokenHolder) ? null : tokenHolder.getAuthToken();

        if (null == authToken)
        {
            GWT.log("No authorization token available - can't order projects.");
            return;
        }

        orderingSvc.requestOrdering(authToken, ORDERING__TS_PROJECTS, new ArrayList<String>(map.keySet()), new AsyncCallback<List<String>>()
        {
            @Override
            public void onSuccess(List<String> result)
            {
                final LinkedHashMap<String, Integer> indexMap = new LinkedHashMap<String, Integer>(result.size());
                for (int i = 0; i < result.size(); ++i)
                {
                    indexMap.put(result.get(i), i);
                }


                TreeMap<String, Double> newResults = new TreeMap<String, Double>(new Comparator<String>()
                    {
                        @Override
                        public int compare(String o1, String o2)
                        {
                            Integer o1Index = indexMap.get(o1);
                            Integer o2Index = indexMap.get(o2);

                            o1Index = null == o1Index ? Integer.MAX_VALUE : o1Index;
                            o2Index = null == o2Index ? Integer.MAX_VALUE : o2Index;

                            return o1Index.compareTo(o2Index);
                        }
                    });

                newResults.putAll(leafTotals);

                leafTotals.clear();
                leafTotals.putAll(newResults);

                onLeafTotalsOrdered();

            }

            @Override
            public void onFailure(Throwable caught)
            {
                GWT.log("Failed getting ordering for projects: " + caught.getMessage(), caught);
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


    private final Map<String, Double> leafTotals = new LinkedHashMap<String, Double>();

    private void redrawTable()
    {
        clearAndInstallHeaders();

        RowMaker rowMaker = new RowMaker(grandTotal, 1);
        for (Tree row: results.values())
        {
            row.accept(rowMaker);
        }
    }

    private void calcLeafTotals()
    {
        final LeafOnlyTotalingVisitor leafTotaler = new LeafOnlyTotalingVisitor();
        for (Tree row: results.values())
        {
            row.accept(leafTotaler);
        }

        leafTotals.clear();
        leafTotals.putAll(leafTotaler.getTotals());

        onLeafTotalsChanged();
    }

    private double grandTotal = 0.;

    private void drawProjects(final double total, final Map<String, Double> projectMap)
    {
        int rowi = 1;
        Set<Entry<String, Double>> leafTotals = projectMap.entrySet();
        int rowmax = leafTotals.size();
        Double totalTotal = 0.;
        Double scaledTotal = 0.;
        for (Entry<String, Double> p: leafTotals)
        {
            int coli = 0;

            projectTable.getRowFormatter().addStyleName(rowi, (rowi % 2) == 0 ? "evenRow" : "oddRow");

            if (rowi < rowmax)
            {
                Anchor moveDownLink = new Anchor("v");
                final int fRowi = rowi;
                moveDownLink.addClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent event)
                    {
                        drawProjects(total, moveRow(projectMap, fRowi - 1, 1));
                    }
                });
                projectTable.setWidget(rowi, coli, moveDownLink);
            }
            ++coli;

            if (1 < rowi)
            {
                Anchor moveUpLink = new Anchor("^");
                final int fRowi = rowi;
                moveUpLink.addClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent event)
                    {
                        drawProjects(total, moveRow(projectMap, fRowi - 1, -1));
                    }
                });
                projectTable.setWidget(rowi, coli, moveUpLink);
            }
            ++coli;


            projectTable.getCellFormatter().setAlignment(rowi, coli, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
            projectTable.setWidget(rowi, coli++, new Label(p.getKey()));

            totalTotal += p.getValue();
            projectTable.setWidget(rowi, coli, new Label(messages.direct(p.getValue())));
            projectTable.getCellFormatter().setAlignment(rowi, coli, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            projectTable.getCellFormatter().removeStyleName(rowi, coli, "totalsRow");
            ++coli;

            if (scaleCheckBox.getValue())
            {
                Double target = Double.valueOf(scaleToTextBox.getText());
                double scaled = p.getValue() / total * target;
                scaledTotal += scaled;
                projectTable.setWidget(rowi, coli, new Label(messages.grandTotalScaled(scaled)));
                projectTable.getCellFormatter().setAlignment(rowi, coli, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
                projectTable.getCellFormatter().removeStyleName(rowi, coli, "totalsRow");
                coli++;
            }

            ++rowi;
        }

        projectTable.setWidget(rowi, 3, new Label(messages.direct(totalTotal)));
        projectTable.getCellFormatter().setAlignment(rowi, 3, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        projectTable.getCellFormatter().addStyleName(rowi, 3, "totalsRow");

        if (scaleCheckBox.getValue())
        {
            projectTable.setWidget(rowi, 4, new Label(messages.grandTotalScaled(scaledTotal)));
            projectTable.getCellFormatter().addStyleName(rowi, 4, "totalsRow");
            projectTable.getCellFormatter().setAlignment(rowi, 4, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
        }

    }

    protected Map<String, Double> moveRow(Map<String, Double> map, int rowi, int rel)
    {
        Map<String, Double> reorder = new Reorderer<String, Double>().reorder(map, rowi, rel);

        String authToken = (null == tokenHolder) ? null : tokenHolder.getAuthToken();

        if (null == authToken)
        {
            GWT.log("No authorization token available - can't order projects.");
        }
        else
        {
            final ArrayList<String> newOrder = new ArrayList<String>(reorder.keySet());
            orderingSvc.setOrdering(authToken, ORDERING__TS_PROJECTS, newOrder, new AsyncCallback<Void>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    GWT.log("Setting ordering for '" + ORDERING__TS_PROJECTS + "' failed: " + caught.getMessage(), caught);
                }

                @Override
                public void onSuccess(Void result)
                {
//                    GWT.log("Ordering set for '" + ORDERING__TS_PROJECTS + "': " + newOrder.toString());
                }
            });
        }

        return reorder;
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
        orderingCheckBox.setValue("true".equals(Cookies.getCookie(PrefKeys.AutoOrder)));
    }

    private void writePrefs()
    {
        Cookies.setCookie(PrefKeys.Scale, scaleCheckBox.getValue() ? "true" : "false", PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.ScaleTo, scaleToTextBox.getValue(), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKeys.AutoOrder, orderingCheckBox.getValue() ? "true" : "false", PrefHelper.createDateSufficientlyInTheFuture());
    }
}
