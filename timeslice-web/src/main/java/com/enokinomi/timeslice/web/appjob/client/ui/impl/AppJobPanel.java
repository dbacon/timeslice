package com.enokinomi.timeslice.web.appjob.client.ui.impl;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanel;
import com.enokinomi.timeslice.web.appjob.client.ui.api.IAppJobPanelListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class AppJobPanel extends ResizeComposite implements IAppJobPanel
{
    private final AppJobPanelConstants constants = GWT.create(AppJobPanelConstants.class);

    private final FlexTable tab = new FlexTable();
    private final FlexTable results = new FlexTable();
    private final ScrollPanel resultScroller = new ScrollPanel(results);

    private List<IAppJobPanelListener> listeners = new ArrayList<IAppJobPanelListener>();

    @Override
    public Widget asWidget() { return this; }

    @Inject
    AppJobPanel()
    {
        HorizontalPanel hp = new HorizontalPanel();

        hp.add(new Button(constants.refreshJobList(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                fireJobListRefreshRequested();
            }
        }));

        hp.add(new Button(constants.clearResults(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                results.removeAllRows();
                addHeaders();
            }
        }));

        results.addStyleDependentName("tsMathTable");

        addHeaders();

        VerticalPanel vp = new VerticalPanel();
        vp.add(hp);
        vp.add(tab);

        SplitLayoutPanel dp = new SplitLayoutPanel();
        dp.addNorth(new ScrollPanel(vp), 300);
        dp.add(resultScroller);

        initWidget(dp);
    }

    protected void addHeaders()
    {
        results.getRowFormatter().setStylePrimaryName(0, "tsTableHeader");
        results.setWidget(0, 0, new HTML(constants.jobId(), false));
        results.setWidget(0, 1, new HTML(constants.status(), false));
        results.setWidget(0, 2, new HTML(constants.description(), false));
    }

    @Override
    public void addResult(String jobId, String status, String result)
    {
        int row = results.getRowCount();
        results.getRowFormatter().addStyleName(row, (row % 2) == 0 ? "evenRow" : "oddRow");
        results.setWidget(row, 0, new Label(jobId));
        results.setWidget(row, 1, new Label(status));
        results.setWidget(row, 2, new Label(result));

        resultScroller.scrollToBottom();
    }

    @Override
    public void addListener(IAppJobPanelListener listener)
    {
        if (null != listener)
        {
            listeners.add(listener);
        }
    }

    protected void fireAppJobRequested(String jobId)
    {
        for (IAppJobPanelListener listener: listeners)
        {
            listener.appJobRequested(jobId);
        }
    }

    protected void fireJobListRefreshRequested()
    {
        for (IAppJobPanelListener listener: listeners)
        {
            listener.appJobListRefreshRequested();
        }
    }

    @Override
    public void redisplayJobIds(List<String> jobIds)
    {
        tab.clear();

        int row = 0;
        for (final String jobId: jobIds)
        {
            int col = 0;
            final int fRow = row;

            Button execButton = new Button(constants.execute(), new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    fireAppJobRequested(jobId);
                }
            });

            execButton.addStyleName("execButton");

            execButton.addMouseOverHandler(new MouseOverHandler()
            {
                @Override
                public void onMouseOver(MouseOverEvent event)
                {
                    tab.getRowFormatter().addStyleName(fRow, "hoveredOn");
                }
            });
            execButton.addMouseOutHandler(new MouseOutHandler()
            {
                @Override
                public void onMouseOut(MouseOutEvent event)
                {

                    tab.getRowFormatter().removeStyleName(fRow, "hoveredOn");
                }
            });

            tab.setWidget(row, col++, new Label(jobId));
            tab.setWidget(row, col++, execButton);

            ++row;
        }
    }
}
