package bacond.timeslice.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bacond.timeslice.web.gwt.client.beans.TaskTotal;
import bacond.timeslice.web.gwt.client.widget.ParamPanel.IParamChangedListener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReportPanel extends ResizeComposite
{
	private final ParamPanel params = new ParamPanel();
	private final Button refreshButton = new Button("Refresh");
	private final Button persistButton = new Button("Persist");
	private final TextBox persistAsName = new TextBox();
	private final Label persisted = new Label();
	private FlexTable resultsTable = new FlexTable();

	private static class PrefKey
	{
		public static final String Starting = "timeslice.report.params.starting";
		public static final String Ending = "timeslice.report.params.ending";
        public static final String IgnoreStrings = "timeslice.report.ignorestrings";
        public static final String AllowStrings = "timeslice.report.allowstrings";
	}

	public static interface IReportPanelListener
	{
	    void refreshRequested(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords);
        void persistRequested(String persistAsName, String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords);
	}

	private ArrayList<IReportPanelListener> listeners = new ArrayList<IReportPanelListener>();

	public void addReportPanelListener(IReportPanelListener listener)
	{
	    if (null != listener)
	    {
	        listeners.add(listener);
	    }
	}

	protected void fireRefreshRequested(String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
	{
	    for (IReportPanelListener listener: listeners)
	    {
	        listener.refreshRequested(startingTimeText, endingTimeText, allowWords, ignoreWords);
	    }
	}

    protected void firePersistRequested(String persistAsName, String startingTimeText, String endingTimeText, List<String> allowWords, List<String> ignoreWords)
    {
        for (IReportPanelListener listener: listeners)
        {
            listener.persistRequested(persistAsName, startingTimeText, endingTimeText, allowWords, ignoreWords);
        }
    }

	private void readPrefs()
	{
		params.getStartingTime().setText(Cookies.getCookie(PrefKey.Starting));
		params.getEndingTime().setText(Cookies.getCookie(PrefKey.Ending));
		params.getIgnoreWords().setText(Cookies.getCookie(PrefKey.IgnoreStrings));
		params.getAllowWords().setText(Cookies.getCookie(PrefKey.AllowStrings));

		if (params.getEndingTime().getText().trim().isEmpty())
		{
			params.getEndingTime().setText(ParamPanel.HumanFormat.format(new Date()));
		}

		if (params.getStartingTime().getText().trim().isEmpty())
		{
			params.getStartingTime().setText(ParamPanel.HumanFormat.format(new Date()));
		}

		params.update();
	}

	private void writePrefs()
	{
		Cookies.setCookie(PrefKey.Starting, params.getSelectedStartingTime());
		Cookies.setCookie(PrefKey.Ending, params.getSelectedEndingTime());
        Cookies.setCookie(PrefKey.IgnoreStrings, params.getIgnoreWords().getText());
        Cookies.setCookie(PrefKey.AllowStrings, params.getAllowWords().getText());
	}

	public ReportPanel()
	{
		params.addParamChangedListener(new IParamChangedListener()
		{
			public void paramChanged(ParamPanel source)
			{
				writePrefs();
				fireRefreshRequested(
                    params.getStartingTimeRendered().getText(),
                    params.getEndingTimeRendered().getText(),
                    Arrays.asList(params.getAllowWords().getText().split(",")),
                    Arrays.asList(params.getIgnoreWords().getText().split(",")));
				//reselectData();
			}
		});

		refreshButton.setAccessKey('r');
		refreshButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                fireRefreshRequested(
                        params.getStartingTimeRendered().getText(),
                        params.getEndingTimeRendered().getText(),
                        Arrays.asList(params.getAllowWords().getText().split(",")),
                        Arrays.asList(params.getIgnoreWords().getText().split(",")));
            }
        });

		persistButton.setAccessKey('p');
		persistButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                firePersistRequested(
                        renderPersistName(),
                        params.getStartingTimeRendered().getText(),
                        params.getEndingTimeRendered().getText(),
                        Arrays.asList(params.getAllowWords().getText().split(",")),
                        Arrays.asList(params.getIgnoreWords().getText().split(",")));
            }
        });

		persistAsName.setText("full-day-%D");
		persistAsName.setTitle("%D - selected full day;  %S - starting date/time;  %E - ending date/time");
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(refreshButton);
		buttonPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		buttonPanel.add(persistAsName);
		buttonPanel.add(persistButton);
		buttonPanel.add(persisted);

		VerticalPanel vp = new VerticalPanel();
		vp.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
		vp.add(params);
		vp.add(buttonPanel);

		SplitLayoutPanel dp = new SplitLayoutPanel();
		dp.addNorth(vp, 180);
		dp.add(new ScrollPanel(resultsTable));

		readPrefs();

		initWidget(dp);
	}

	protected String renderPersistName()
	{
	    return persistAsName.getText()
	        .replaceAll("%D", params.getFullDaySelected())
	        .replaceAll("%S", params.getStartingTimeRendered().getText())
	        .replaceAll("%E", params.getEndingTimeRendered().getText());
	}

	public void updateResults(List<TaskTotal> report)
	{
	    Collections.sort(report, Collections.reverseOrder(new Comparator<TaskTotal>()
				{
					public int compare(TaskTotal o1, TaskTotal o2)
					{
						return o1.getHours().compareTo(o2.getHours());
					}
				}));


		resultsTable.removeAllRows();
		resultsTable.setCellSpacing(5);
		int row = 0;
		int col = 0;

		resultsTable.setWidget(row, col++, new HTML("<b><u>Who</u></b>", false));
		resultsTable.setWidget(row, col++, new HTML("<b><u>Hours</u></b>", false));
		resultsTable.setWidget(row, col++, new HTML("<b><u>%</u></b>", false));
		resultsTable.setWidget(row, col++, new HTML("<b><u>What</u></b>", false));
		resultsTable.setWidget(row, col++, new HTML("<b><u>Code</u></b>", false));

		row++;

		for (TaskTotal reportRow: report)
		{
			col = 0;

			resultsTable.setText(row, col++, reportRow.getWho());
			resultsTable.setText(row, col++, NumberFormat.getDecimalFormat().format(reportRow.getHours()));
			resultsTable.setText(row, col++, NumberFormat.getPercentFormat().format(reportRow.getPercentage()));
			resultsTable.setText(row, col++, reportRow.getWhat());
			resultsTable.setText(row, col++, "" + reportRow.getWhat().hashCode());

			row++;
		}
	}

	public void setPersisted(String persistedName)
	{
	    persisted.setText(persistedName);
	}
}
