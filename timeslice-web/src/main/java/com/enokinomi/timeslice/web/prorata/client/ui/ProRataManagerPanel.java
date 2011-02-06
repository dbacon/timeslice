package com.enokinomi.timeslice.web.prorata.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.IOnAuthenticated;
import com.enokinomi.timeslice.web.prorata.client.core.Group;
import com.enokinomi.timeslice.web.prorata.client.core.GroupComponent;
import com.enokinomi.timeslice.web.prorata.client.core.IProRataSvc;
import com.enokinomi.timeslice.web.prorata.client.core.IProRataSvcAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

public class ProRataManagerPanel extends Composite
{
    private final ProRataManagerPanelConstants constants = GWT.create(ProRataManagerPanelConstants.class);

    private final VerticalPanel prorataManagePanel = new VerticalPanel();
    private final FlexTable groupInfoTable = new FlexTable();
    private final TextBox groupBox = new TextBox();
    private final TextBox targetBox = new TextBox();
    private final TextBox weightBox = new TextBox();
    private final Button b = new Button(constants.addNew());

    private final TextArea rulesTextArea = new TextArea();


    private final IProRataSvcAsync prorataSvc = GWT.create(IProRataSvc.class);
    private final ILoginSupport loginSupport;

    public static interface Listener
    {
        void groupsChanged();
    }

    private final List<Listener> listeners = new ArrayList<ProRataManagerPanel.Listener>();

    public void addListener(Listener l)
    {
        if (null != l)
        {
            listeners.add(l);
        }
    }

    public void removeListener(Listener l)
    {
        if (null != l)
        {
            listeners.remove(l);
        }
    }

    protected void fireGroupsChanged()
    {
        for (Listener l: listeners)
        {
            l.groupsChanged();
        }
    }

    @Inject
    ProRataManagerPanel(ILoginSupport loginSupport)
    {
        this.loginSupport = loginSupport;

        b.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                new IOnAuthenticated()
                {
                    @Override
                    public void runAsync()
                    {
                        addGroupComponent2(groupBox.getText(), targetBox.getText(), Double.valueOf(weightBox.getText()));
                    }
                };
            }
        });

        groupBox.addKeyUpHandler(new KeyUpHandler()
        {
            @Override
            public void onKeyUp(KeyUpEvent event)
            {
                b.setEnabled(!targetBox.getText().trim().isEmpty() && !groupBox.getText().trim().isEmpty());
            }
        });

        targetBox.addKeyUpHandler(new KeyUpHandler()
        {
            @Override
            public void onKeyUp(KeyUpEvent event)
            {
                b.setEnabled(!targetBox.getText().trim().isEmpty() && !groupBox.getText().trim().isEmpty());
            }
        });

        weightBox.setText("1");
        weightBox.setWidth("2em");

        b.setEnabled(!targetBox.getText().trim().isEmpty() && !groupBox.getText().trim().isEmpty());


        Button rulesRemoveAllButton = new Button(constants.remove());
        rulesRemoveAllButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                // TODO: keep list of groups, so we can remove them all
                // TODO: add remove-all in svc defn

                List<ParsedRule> parsedRules = parseRules();
                GWT.log("Parsed " + parsedRules.size() + " rule(s).");

                if (0 < parsedRules.size())
                {
                    // TODO: add bulk-load to service definition to avoid a bunch of calls
                    for (final ParsedRule rule: parsedRules)
                    {
                        removeGroupComponent(rule);
                    }

                    // for now, simulate a 1-time update after bulk load
                    new Timer()
                    {
                        @Override
                        public void run()
                        {
                            fireGroupsChanged();
                        }
                    }.schedule(500);
                }
            }
        });

        // TODO: set remove/load button sensitivities and a label depending on if rules are parsed.

        Button rulesLoadButton = new Button(constants.load());
        rulesLoadButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                List<ParsedRule> parsedRules = parseRules();

                if (0 < parsedRules.size())
                {
                    // TODO: add bulk-load to service definition to avoid a bunch of calls
                    for (final ParsedRule rule: parsedRules)
                    {
                        addGroupComponent_inBulk(rule);
                    }

                    // for now, simulate a 1-time update after bulk load
                    new Timer()
                    {
                        @Override
                        public void run()
                        {
                            fireGroupsChanged();
                        }
                    }.schedule(500);
                }
            }

        });

        rulesTextArea.setHeight("20em");
        rulesTextArea.setWidth("30em");

        HorizontalPanel cp = new HorizontalPanel();
        cp.add(rulesRemoveAllButton);
        cp.add(rulesLoadButton);

        VerticalPanel dp = new VerticalPanel();
        dp.add(rulesTextArea);
        dp.add(cp);

        DisclosurePanel loadSaveDiscl = new DisclosurePanel(constants.fullRulesDescription());
        loadSaveDiscl.add(dp);

        VerticalPanel loadSavePanel = new VerticalPanel();
        loadSavePanel.add(loadSaveDiscl);

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(groupInfoTable);
        hp.add(loadSavePanel);
        ScrollPanel scroller = new ScrollPanel(hp);

        prorataManagePanel.add(scroller);

        initWidget(prorataManagePanel);
    }

    private void removeGroupComponent(final ParsedRule rule)
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
                                // dont refresh, we're in bulk.
                                GWT.log("Removed rule: " + rule.parent + " -> " + rule.parent);
                            }
                        }));
            }
        }.runAsync();
    }

    private void addGroupComponent_inBulk(final ParsedRule rule)
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
                                GWT.log("Failed adding rule (s" + rule.parent + " -> " + rule.child + "): " + caught.getMessage());
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                // dont refresh, we're in bulk.
                                GWT.log("Added rule: " + rule.parent + " -> " + rule.child);
                            }
                        }));
            }
        }.runAsync();
    }

    private void addGroupComponent2(final String name, final String target, final Double weight)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                prorataSvc.addGroupComponent(loginSupport.getAuthToken(),
                        name, target, weight,
                        loginSupport.withRetry(this,
                        new AsyncCallback<Void>()
                {
                    @Override
                    public void onFailure(Throwable caught)
                    {
                        GWT.log("Failure adding new group: " + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Void result)
                    {
                        groupBox.setText("");
                        targetBox.setText("");
                        weightBox.setText("1");

                        fireGroupsChanged();
                    }
                }));
            }
        }.runAsync();
    }

    private void addGroupComponent3(final String groupName, final String target, final Double weight)
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
                                GWT.log("Failure adding new group: " + caught.getMessage());
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                refresh();
                                fireGroupsChanged();
                            }
                        }));
            }
        }.runAsync();
    }

    private void removeGroupComponent3(final GroupComponent component)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                prorataSvc.removeGroupComponent(loginSupport.getAuthToken(),
                        component.getGroupName(), component.getName(),
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
                                refresh();
                                fireGroupsChanged();
                            }
                        }));
            }
        }.runAsync();
    }

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

    private List<ParsedRule> parseRules()
    {
        List<ParsedRule> parsedRules = new ArrayList<ParsedRule>();
        String[] ruleLines = rulesTextArea.getText().split("\n");
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

    public void clear()
    {
        updateBulkTextArea(Arrays.<Group>asList());
        groupInfoTable.removeAllRows();
    }

    public void refresh()
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
                                updateBulkTextArea(result);

                                groupInfoTable.removeAllRows();

                                int row = 0;

                                for (Group group: result)
                                {
                                    final String groupName = group.getName();

                                    final TextBox targetBox = new TextBox();
                                    final TextBox weightBox = new TextBox();
                                    weightBox.setText("1");
                                    weightBox.setWidth("2em");

                                    final Button addButton = new Button(constants.add(), new ClickHandler()
                                    {
                                        @Override
                                        public void onClick(ClickEvent event)
                                        {
                                            addGroupComponent3(groupName, targetBox.getText(), Double.valueOf(weightBox.getText()));
                                        }

                                    });

                                    targetBox.addKeyUpHandler(new KeyUpHandler()
                                    {
                                        @Override
                                        public void onKeyUp(KeyUpEvent event)
                                        {
                                            addButton.setEnabled(!targetBox.getText().trim().isEmpty());
                                        }
                                    });
                                    addButton.setEnabled(!targetBox.getText().trim().isEmpty());


                                    groupInfoTable.setText(row, 0, groupName);
                                    groupInfoTable.setWidget(row, 1, targetBox);
                                    groupInfoTable.setWidget(row, 2, weightBox);
                                    groupInfoTable.setWidget(row, 3, addButton);
                                    ++row;

                                    for (final GroupComponent component: group.getComponents())
                                    {
                                        int col = 1;
                                        groupInfoTable.setText(row, col++, component.getName());
                                        groupInfoTable.setText(row, col++, component.getWeight().toString());
                                        Anchor anchor = new Anchor(constants.deleteTextIcon());
                                        groupInfoTable.setWidget(row, col++, anchor);
                                        anchor.addClickHandler(new ClickHandler()
                                        {
                                            @Override
                                            public void onClick(ClickEvent event)
                                            {
                                                removeGroupComponent3(component);
                                            }
                                        });

                                        ++row;
                                    }
                                }

                                groupInfoTable.setWidget(row, 0, groupBox);
                                groupInfoTable.setWidget(row, 1, targetBox);
                                groupInfoTable.setWidget(row, 2, weightBox);
                                groupInfoTable.setWidget(row, 3, b);

                                ++row;

                            }
                        }));
            }
        }.runAsync();
    }

    protected void updateBulkTextArea(List<Group> result)
    {
        StringBuilder sb = new StringBuilder();
        for (Group group: result)
        {
            for (GroupComponent comp: group.getComponents())
            {
                sb.append(group.getName())
                    .append("|").append(comp.getName())
                    .append("|").append(comp.getWeight())
                    .append('\n');
            }
        }

        rulesTextArea.setText(sb.toString());
    }

}
