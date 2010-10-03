package com.enokinomi.timeslice.web.gwt.client.prorata.ui;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.controller.IAuthTokenHolder;
import com.enokinomi.timeslice.web.gwt.client.core.Pair;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.Group;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.GroupComponent;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvc;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvcAsync;
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
    private IAuthTokenHolder tokenHolder;

    public void setAuthTokenHolder(IAuthTokenHolder tokenHolder)
    {
        this.tokenHolder = tokenHolder;
    }

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

    public ProRataManagerPanel()
    {
        b.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                prorataSvc.addGroupComponent(tokenHolder.getAuthToken(), groupBox.getText(), targetBox.getText(), Double.valueOf(weightBox.getText()), new AsyncCallback<Void>()
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

                        fireGroupsChanged();
                    }
                });
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

        weightBox.setWidth("2em");

        b.setEnabled(!targetBox.getText().trim().isEmpty() && !groupBox.getText().trim().isEmpty());


        Button rulesRemoveAllButton = new Button("Remove");
        rulesRemoveAllButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                // TODO: keep list of groups, so we can remove them all
                // TODO: add remove-all in svc defn

                List<Pair<String,String>> parsedRules = parseRules();
                GWT.log("Parsed " + parsedRules.size() + " rule(s).");

                if (0 < parsedRules.size())
                {
                    // TODO: add bulk-load to service definition to avoid a bunch of calls
                    for (final Pair<String, String> rule: parsedRules)
                    {
                        prorataSvc.removeGroupComponent(tokenHolder.getAuthToken(), rule.first, rule.second, new AsyncCallback<Void>()
                            {
                                @Override
                                public void onFailure(Throwable caught)
                                {
                                    GWT.log("Failed removing rule (s" + rule.first + " -> " + rule.second + "): " + caught.getMessage());
                                }

                                @Override
                                public void onSuccess(Void result)
                                {
                                    // dont refresh, we're in bulk.
                                    GWT.log("Removed rule: " + rule.first + " -> " + rule.second);
                                }
                            });
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

        Button rulesLoadButton = new Button("Load");
        rulesLoadButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                List<Pair<String,String>> parsedRules = parseRules();

                if (0 < parsedRules.size())
                {
                    // TODO: add bulk-load to service definition to avoid a bunch of calls
                    for (final Pair<String, String> rule: parsedRules)
                    {
                        prorataSvc.addGroupComponent(tokenHolder.getAuthToken(), rule.first, rule.second, Double.valueOf(weightBox.getText()), new AsyncCallback<Void>()
                            {
                                @Override
                                public void onFailure(Throwable caught)
                                {
                                    GWT.log("Failed adding rule (s" + rule.first + " -> " + rule.second + "): " + caught.getMessage());
                                }

                                @Override
                                public void onSuccess(Void result)
                                {
                                    // dont refresh, we're in bulk.
                                    GWT.log("Added rule: " + rule.first + " -> " + rule.second);
                                }
                            });
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

        DisclosurePanel loadSaveDiscl = new DisclosurePanel("Bulk/Raw Rule Editing");
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

    private List<Pair<String, String>> parseRules()
    {
        List<Pair<String,String>> parsedRules = new ArrayList<Pair<String, String>>();
        String[] ruleLines = rulesTextArea.getText().split("\n");
        for (String ruleLine: ruleLines)
        {
            String[] parentChild = ruleLine.split("\\|");
            if (2 == parentChild.length)
            {
                parsedRules.add(Pair.create(parentChild[0], parentChild[1]));
            }
        }
        return parsedRules;
    }

    public void refresh()
    {
        prorataSvc.listAllGroupInfo(tokenHolder.getAuthToken(), new AsyncCallback<List<Group>>()
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
                    weightBox.setWidth("2em");

                    final Button addButton = new Button(constants.add(), new ClickHandler()
                    {
                        @Override
                        public void onClick(ClickEvent event)
                        {
                            prorataSvc.addGroupComponent(tokenHolder.getAuthToken(), groupName, targetBox.getText(), Double.valueOf(weightBox.getText()), new AsyncCallback<Void>()
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
                                    });
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
                                prorataSvc.removeGroupComponent(tokenHolder.getAuthToken(), component.getGroupName(), component.getName(), new AsyncCallback<Void>()
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
                                });
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
        });
    }

    protected void updateBulkTextArea(List<Group> result)
    {
        StringBuilder sb = new StringBuilder();
        for (Group group: result)
        {
            for (GroupComponent comp: group.getComponents())
            {
                sb.append(group.getName()).append("|").append(comp.getName()).append('\n');
            }
        }

        rulesTextArea.setText(sb.toString());
    }

}
