package com.enokinomi.timeslice.web.appjob.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AppJobPanel extends ResizeComposite
{
    private final AppJobPanelConstants constants = GWT.create(AppJobPanelConstants.class);

    private final FlexTable tab = new FlexTable();
    private final FlexTable results = new FlexTable();
    private final ScrollPanel resultScroller = new ScrollPanel(results);

    private List<Listener> listeners = new ArrayList<Listener>();

    public AppJobPanel()
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

        addHeaders();

        ScrollPanel jobScrollPanel = new ScrollPanel(tab);
        jobScrollPanel.setHeight("15em");

        VerticalPanel vp = new VerticalPanel();
        vp.add(hp);
        vp.add(jobScrollPanel);

        DockLayoutPanel dp = new DockLayoutPanel(Unit.EM);
        dp.addNorth(vp, 17);
        dp.add(resultScroller);

        initWidget(dp);
    }

    protected void addHeaders()
    {
        results.setWidget(0, 0, new HTML("<b><u>" + constants.jobId() + "</u></b>", false));
        results.setWidget(0, 1, new HTML("<b><u>" + constants.status() + "</u></b>", false));
        results.setWidget(0, 2, new HTML("<b><u>" + constants.description() + "</b></u>", false));
    }

    public void addResult(String jobId, String status, String result)
    {
        int row = results.getRowCount();
        results.setWidget(row, 0, new Label(jobId));
        results.setWidget(row, 1, new Label(status));
        results.setWidget(row, 2, new Label(result));

        resultScroller.scrollToBottom();
    }

    public interface Listener
    {
        void appJobRequested(String jobId);

        void appJobListRefreshRequested();
    }


    public void addListener(Listener listener)
    {
        if (null != listener)
        {
            listeners.add(listener);
        }
    }

    protected void fireAppJobRequested(String jobId)
    {
        for (Listener listener: listeners)
        {
            listener.appJobRequested(jobId);
        }
    }

    protected void fireJobListRefreshRequested()
    {
        for (Listener listener: listeners)
        {
            listener.appJobListRefreshRequested();
        }
    }

    public void redisplayJobIds(List<String> jobIds)
    {
        tab.clear();

        int row = 0;
        for (final String jobId: jobIds)
        {
            int col = 0;

            tab.setWidget(row, col++, new Label(jobId));
            tab.setWidget(row, col++, new Button(constants.execute(), new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    fireAppJobRequested(jobId);
                }
            }));

            ++row;
        }
    }
}
