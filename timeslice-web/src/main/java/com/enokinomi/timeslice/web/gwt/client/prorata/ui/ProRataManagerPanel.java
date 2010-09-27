package com.enokinomi.timeslice.web.gwt.client.prorata.ui;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.controller.IAuthTokenHolder;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.GroupComponent;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvc;
import com.enokinomi.timeslice.web.gwt.client.prorata.core.IProRataSvcAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProRataManagerPanel extends Composite
{
    private final ProRataManagerPanelConstants constants = GWT.create(ProRataManagerPanelConstants.class);

    private final VerticalPanel prorataManagePanel = new VerticalPanel();
    private final FlexTable groupInfoTable = new FlexTable();
    private final TextBox groupBox = new TextBox();
    private final TextBox targetBox = new TextBox();
    final Button b = new Button(constants.addNew());

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
                prorataSvc.addGroupComponent(tokenHolder.getAuthToken(), groupBox.getText(), targetBox.getText(), "1", new AsyncCallback<Void>()
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

        b.setEnabled(!targetBox.getText().trim().isEmpty() && !groupBox.getText().trim().isEmpty());

        VerticalPanel vp = new VerticalPanel();
        vp.add(groupInfoTable);
        ScrollPanel scroller = new ScrollPanel(vp);

        prorataManagePanel.add(scroller);

        initWidget(prorataManagePanel);
    }

    public void refresh()
    {
        prorataSvc.listAllGroupInfo(tokenHolder.getAuthToken(), new AsyncCallback<List<List<GroupComponent>>>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                GWT.log("Updating groups failed.");
            }

            @Override
            public void onSuccess(List<List<GroupComponent>> result)
            {
                GWT.log("Building group panel.");

                groupInfoTable.removeAllRows();

                int row = 0;

                for (List<GroupComponent> groupComponents: result)
                {
                    if (result.size() > 0)
                    {
                        final String groupName = groupComponents.get(0).getGroupName();

                        final TextBox targetBox = new TextBox();

                        final Button addButton = new Button(constants.add(), new ClickHandler()
                        {
                            @Override
                            public void onClick(ClickEvent event)
                            {
                                prorataSvc.addGroupComponent(tokenHolder.getAuthToken(), groupName, targetBox.getText(), "1", new AsyncCallback<Void>()
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
                        groupInfoTable.setWidget(row, 2, addButton);
                        ++row;

                        for (final GroupComponent component: groupComponents)
                        {
                            int col = 1;
                            groupInfoTable.setText(row, col++, component.getName());
                            groupInfoTable.setText(row, col++, component.getWeight());
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
                    else
                    {
                        GWT.log("Got group back w/ no components?!");
                    }

                }

                groupInfoTable.setWidget(row, 0, groupBox);
                groupInfoTable.setWidget(row, 1, targetBox);
                groupInfoTable.setWidget(row, 2, b);

                ++row;

            }
        });
    }

}
