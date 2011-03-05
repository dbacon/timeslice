package com.enokinomi.timeslice.web.prorata.client.presenter.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.util.Checks;
import com.enokinomi.timeslice.web.core.client.util.ListenerManager;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.IOnAuthenticated;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.LoginListener;
import com.enokinomi.timeslice.web.ordering.client.core.IOrderingSvcAsync;
import com.enokinomi.timeslice.web.prorata.client.core.Group;
import com.enokinomi.timeslice.web.prorata.client.core.GroupComponent;
import com.enokinomi.timeslice.web.prorata.client.core.IProrataSvcAsync;
import com.enokinomi.timeslice.web.prorata.client.presenter.api.IProrataManagerPresenter;
import com.enokinomi.timeslice.web.prorata.client.tree.Branch;
import com.enokinomi.timeslice.web.prorata.client.tree.IVisitor;
import com.enokinomi.timeslice.web.prorata.client.tree.Leaf;
import com.enokinomi.timeslice.web.prorata.client.tree.LeafOnlyTotalingVisitor;
import com.enokinomi.timeslice.web.prorata.client.tree.MapRuleSource;
import com.enokinomi.timeslice.web.prorata.client.tree.Tree;
import com.enokinomi.timeslice.web.prorata.client.ui.impl.ProjectProrataTreePanel;
import com.enokinomi.timeslice.web.prorata.client.ui.impl.ProjectProrataTreePanel.Row;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class ProrataManagerPresenter implements IProrataManagerPresenter
{
    private static final String ORDERING__TS_PROJECTS = "ts-projects";

    private final ILoginSupport loginSupport;
    private final IProrataSvcAsync prorataSvc;
    private final IOrderingSvcAsync orderingSvc;

    private final List<AssignedTaskTotal> itemsCache = new ArrayList<AssignedTaskTotal>();
    private double grandTotal = 0.;
    private final List<Group> groupInfo = new ArrayList<Group>();
    private final List<Row> rows = new ArrayList<ProjectProrataTreePanel.Row>();
    private final Map<String, Double> leafTotals = new LinkedHashMap<String, Double>();
    private List<String> ordering = new ArrayList<String>();

    private static class ParsedRule
    {
        public String parent;
        public String child;
        public Double weight;

        public ParsedRule(String parent, String child, Double weight)
        {
            this.parent = parent;
            this.child = child;
            this.weight = weight;
        }
    }

    @Override
    public List<Group> getGroupInfo()
    {
        return groupInfo;
    }

    @Override
    public Map<String, Double> getLeafTotals()
    {
        return leafTotals;
    }

    @Override
    public double getGrandTotal()
    {
        return grandTotal;
    }

    @Override
    public List<Row> getRows()
    {
        return rows;
    }

    private final ListenerManager<Listener> listenerMgr = new ListenerManager<Listener>();

    @Override public Registration addListener(Listener listener) { return listenerMgr.addListener(listener); }

    @Override
    public void addGroupComponent(final String groupName, final String target, final Double weight)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                prorataSvc.addGroupComponent(loginSupport.getAuthToken(),
                        groupName, target, weight,
                        loginSupport.withRetry(this, new AsyncCallback<Void>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                fireAddFailed(caught.getMessage());
                                GWT.log("Failure adding new group: " + caught.getMessage());
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                onAddComplete(groupName, target);
                            }
                        }));
            }
        }.runAsync();
    }

    protected void fireTasksUpdated()
    {
        for (Listener listener: listenerMgr.getListeners())
        {
            listener.tasksUpdated();
        }
    }

    protected void onTasksUpdate()
    {
        fireTasksUpdated();
    }

    private void updateProrata(List<AssignedTaskTotal> items)
    {
        if (items != null)
        {
            itemsCache.clear();
            itemsCache.addAll(items);
        }

        updateProrataFromCache();
    }

    /**
     * Could be due to pro-rata groupings changing, or task-data changing.
     * Both mean all these representations needs re-rendering.
     */
    private void updateProrataFromCache()
    {
        //
        //  1st pass: grand totaling, by-item-totaling into buckets as tree roots.
        //
        grandTotal = 0.;
        Map<String, Tree> results = new LinkedHashMap<String, Tree>();

        for (AssignedTaskTotal item: itemsCache)
        {
            // bucket by billed-to, accumulating hours.
            Leaf leaf = (Leaf) Checks.mapNullTo(results.get(item.getBilledTo()), new Leaf(item.getBilledTo(), 1., 0.));
            Leaf newLeaf = new Leaf(leaf.getName(), leaf.getWeight(), leaf.getValue() + item.getHours());
            results.put(newLeaf.getName(), newLeaf);

            // bucket by 1, accumulating all hours.
            grandTotal += item.getHours();
        }


        //
        // 2nd pass: expand tree roots according to pro-rata rules
        //

        MapRuleSource ruleSource = new MapRuleSource();
        for (Group group: groupInfo)
        {
            ruleSource.add(group.getName(), group.getComponents().toArray(new GroupComponent[group.getComponents().size()]));
        }

        Map<String, Tree> newResults = new LinkedHashMap<String, Tree>();

        for (Tree root: results.values())
        {
            Tree expanded = root.expand(ruleSource, 0.0001);
            newResults.put(expanded.getName(), expanded);
        }
        results.clear();
        results.putAll(newResults);


        //
        // 3rd pass: expanded tree traversals
        //  leaf totaling for report page
        //  row generation for pro-rata in project list
        //

        LeafOnlyTotalingVisitor leafTotaler = new LeafOnlyTotalingVisitor();

        RowMaker rowMaker = new RowMaker(grandTotal, 1, rows);
        rows.clear();

        for (Tree result: results.values())
        {
            result.accept(rowMaker);
            result.accept(leafTotaler);
        }

        leafTotals.clear();
        leafTotals.putAll(leafTotaler.getTotals());

        //
        // pass 4: re-order leafTotals

        final LinkedHashMap<String, Integer> indexMap = new LinkedHashMap<String, Integer>(ordering.size());
        for (int i = 0; i < ordering.size(); ++i)
        {
            indexMap.put(ordering.get(i), i);
        }

        TreeMap<String, Double> newLeafResults = new TreeMap<String, Double>(new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    Integer o1Index = indexMap.get(o1);
                    Integer o2Index = indexMap.get(o2);

                    if (o1Index == null)
                    {
                        if (o2Index == null)
                        {
                            return o1.compareTo(o2);
                        }
                        else
                        {
                            return 1;
                        }
                    }
                    else
                    {
                        if (o2Index == null)
                        {
                            return 1;
                        }
                        else
                        {
                            return o1Index.compareTo(o2Index);
                        }
                    }
                }
            });

        newLeafResults.putAll(leafTotals);

        leafTotals.clear();
        leafTotals.putAll(newLeafResults);

        onTasksUpdate();
    }

    class RowMaker implements IVisitor<RowMaker>
    {
        private final double total;
        private int rowIndex;
        private final List<Row> destination;

        public RowMaker(double total, int initialRowIndex, List<Row> destination)
        {
            this.total = total;
            this.rowIndex = initialRowIndex;
            this.destination = destination;
        }

        @Override
        public RowMaker visit(Tree t, Branch parent, int currentDepth, int[] siblingCounts, int[] siblingIndexs)
        {
            String parentName = parent == null ? null : parent.getName();

            destination.add(new Row(total, rowIndex, t.getName(), t.getValue(), t.getWeight(), t.isLeaf(), parentName, currentDepth, siblingCounts, siblingIndexs));

            rowIndex++;

            return this;
        }

        public List<Row> getRows()
        {
            return destination;
        }
    };



    @Inject
    ProrataManagerPresenter(ILoginSupport loginSupport, IProrataSvcAsync prorataSvc, IOrderingSvcAsync orderingSvc)
    {
        this.loginSupport = loginSupport;
        this.prorataSvc = prorataSvc;
        this.orderingSvc = orderingSvc;

        loginSupport.addLoginListener(new LoginListener()
        {
            @Override
            public void sessionEnded(boolean retry)
            {
            }

            @Override
            public void newSessionStarted()
            {
                refreshGroupInfo();
                requestOrdering();
            }
        });

        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                refreshGroupInfo();
            }
        });

        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                requestOrdering();
            }
        });

    }

    protected void onGroupsChanged()
    {
        refreshGroupInfo();
    }

    protected void fireAddFailed(String msg) { for (Listener l: listenerMgr.getListeners()) l.addFailed(msg); }
    protected void fireAllGroupInfoChanged(List<Group> result)
    {
        for (Listener listener: listenerMgr.getListeners())
        {
            listener.allGroupInfoChanged(result);
        }
    }

    protected void fireAddComplete(String group, String name)
    {
        for(Listener listener: listenerMgr.getListeners())
        {
            listener.addComplete(group, name);
        }
    }

    protected void fireRemoveComplete(String group, String name)
    {
        for(Listener listener: listenerMgr.getListeners())
        {
            listener.removeComplete(group, name);
        }
    }

    protected void onListAllGroupInfoComplete(List<Group> result)
    {
        groupInfo.clear();
        groupInfo.addAll(result);

        updateProrataFromCache();

        fireAllGroupInfoChanged(result);
    }

    protected void onAddComplete(String group, String name)
    {
        onGroupsChanged();
        fireAddComplete(group, name);
    }

    protected void onLoadAllGroupsComplete()
    {
        onGroupsChanged();
        fireAddComplete("(all)", "(all)");
    }

    protected void onRemoveAllComplete()
    {
        onGroupsChanged();
        fireRemoveComplete("(all)", "(all)");
    }

    protected void onRemoveGroupComponentComplete(String group, String name)
    {
        onGroupsChanged();
        fireRemoveComplete(group, name);
    }

    @Override
    public void loadAllRules(String text)
    {
        List<ProrataManagerPresenter.ParsedRule> parsedRules = parseRules(text);

        if (0 < parsedRules.size())
        {
            // TODO: add bulk-load to service definition to avoid a bunch of calls
            for (final ProrataManagerPresenter.ParsedRule rule: parsedRules)
            {
                addGroupComponent_singleOfBulk(rule);
            }

            // for now, simulate a 1-time update after bulk load
            new Timer()
            {
                @Override
                public void run()
                {
                    onLoadAllGroupsComplete();
                }
            }.schedule(500);
        }
    }

    private List<ProrataManagerPresenter.ParsedRule> parseRules(String text)
    {
        List<ProrataManagerPresenter.ParsedRule> parsedRules = new ArrayList<ProrataManagerPresenter.ParsedRule>();
        String[] ruleLines = text.split("\n");
        for (String ruleLine: ruleLines)
        {
            String[] parentChild = ruleLine.split("\\|");
            if (3 == parentChild.length)
            {
                parsedRules.add(new ParsedRule(parentChild[0], parentChild[1], Double.valueOf(parentChild[2])));
            }
        }
        return parsedRules;
    }

    void addGroupComponent_singleOfBulk(final ProrataManagerPresenter.ParsedRule rule)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                prorataSvc.addGroupComponent(loginSupport.getAuthToken(),
                        rule.parent, rule.child, rule.weight,
                        loginSupport.withRetry(this, new AsyncCallback<Void>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                String msg = "Failed adding rule (s" + rule.parent + " -> " + rule.child + "): " + caught.getMessage();
                                fireAddFailed(msg);
                                GWT.log(msg);
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                // don't refresh, we're in bulk.
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void removeParsedRules(String text)
    {

        // TODO: keep list of groups, so we can remove them all
        // TODO: add remove-all in svc defn

        List<ProrataManagerPresenter.ParsedRule> parsedRules = parseRules(text);

        if (0 < parsedRules.size())
        {
            // TODO: add bulk-load to service definition to avoid a bunch of calls
            for (final ProrataManagerPresenter.ParsedRule rule: parsedRules)
            {
                removeGroupComponent_singleOfBulk(rule);
            }

            // for now, simulate a 1-time update after bulk load
            new Timer()
            {
                @Override
                public void run()
                {
                    onRemoveAllComplete();
                }
            }.schedule(500);
        }
    }

    private void removeGroupComponent_singleOfBulk(final ProrataManagerPresenter.ParsedRule rule)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                prorataSvc.removeGroupComponent(loginSupport.getAuthToken(),
                        rule.parent, rule.child,
                        loginSupport.withRetry(this, new AsyncCallback<Void>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                GWT.log("Failed removing rule (s" + rule.parent + " -> " + rule.child + "): " + caught.getMessage());
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                // don't refresh, we're in bulk.
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void removeGroupComponent(final String group, final String name)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                prorataSvc.removeGroupComponent(loginSupport.getAuthToken(),
                        group, name,
                        loginSupport.withRetry(this, new AsyncCallback<Void>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                GWT.log("Failed to remove group component.");
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                onRemoveGroupComponentComplete(group, name);
                            }
                        }));
            }
        }.runAsync();
    }

    void refreshGroupInfo()
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                prorataSvc.listAllGroupInfo(loginSupport.getAuthToken(),
                        loginSupport.withRetry(this, new AsyncCallback<List<Group>>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                GWT.log("Updating groups failed.");
                            }

                            @Override
                            public void onSuccess(List<Group> result)
                            {
                                onListAllGroupInfoComplete(result);
                            }
                        }));
            }
        }.runAsync();
    }

    private void requestOrdering()
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                orderingSvc.requestOrdering(loginSupport.getAuthToken(),
                        ORDERING__TS_PROJECTS,
                        loginSupport.withRetry(this, new AsyncCallback<List<String>>()
                {
                    @Override
                    public void onSuccess(List<String> result)
                    {
                        onOrderingReceived(result);
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        GWT.log("Failed getting ordering for projects: " + caught.getMessage(), caught);
                    }
                }));
            }
        }.runAsync();
    }

    protected void onOrderingReceived(List<String> result)
    {
        ordering.clear();
        ordering.addAll(result);

        updateProrataFromCache();
    }

    @Override
    public void sendPartialOrderingAssignment(Map<String, Double> projectMap, int i, int j)
    {
        if (j < 0) --j;

        List<String> keyList = new ArrayList<String>(projectMap.keySet());

        String smaller = null;

        if (i+j >= 0)
        {
            smaller = keyList.get(i+j);
        }

        String item = keyList.get(i);

        orderingSvc.setPartialOrdering(loginSupport.getAuthToken(), ORDERING__TS_PROJECTS, smaller, Arrays.asList(item), new AsyncCallback<Void>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                GWT.log("Setting partial order failed.");
            }

            @Override
            public void onSuccess(Void result)
            {
                requestOrdering();
            }
        });

    }

    @Override
    public void setStuff(List<AssignedTaskTotal> report)
    {
        updateProrata(report);
    }

}
