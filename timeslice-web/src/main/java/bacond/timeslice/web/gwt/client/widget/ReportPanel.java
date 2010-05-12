package bacond.timeslice.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bacond.timeslice.web.gwt.client.beans.TaskTotal;
import bacond.timeslice.web.gwt.client.widget.ParamPanel.IParamChangedListener;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReportPanel extends Composite
{
	private final ParamPanel params = new ParamPanel();
	private final VerticalPanel resultPanel = new VerticalPanel();
	private final Button refreshButton = new Button("Refresh");
	private final VerticalPanel chartBit = new VerticalPanel();
	private final Button persistButton = new Button("Persist");
	private final TextBox persistAsName = new TextBox();
	private final Label persisted = new Label();

	private static class PrefKey
	{
		public static final String Starting = "timeslice.report.params.starting";
		public static final String Ending = "timeslice.report.params.ending";
        public static final String IgnoreStrings = "timeslice.report.ignorestrings";
        public static final String AllowStrings = "timeslice.report.allowstrings";
	}

	public static interface IReportPanelListener
	{
	    void refreshRequested(String startingTimeText, String endingTimeText);
        void persistRequested(String persistAsName, String startingTimeText, String endingTimeText);
	}

	private ArrayList<IReportPanelListener> listeners = new ArrayList<IReportPanelListener>();

	public void addReportPanelListener(IReportPanelListener listener)
	{
	    if (null != listener)
	    {
	        listeners.add(listener);
	    }
	}

	protected void fireRefreshRequested(String startingTimeText, String endingTimeText)
	{
	    for (IReportPanelListener listener: listeners)
	    {
	        listener.refreshRequested(startingTimeText, endingTimeText);
	    }
	}

    protected void firePersistRequested(String persistAsName, String startingTimeText, String endingTimeText)
    {
        for (IReportPanelListener listener: listeners)
        {
            listener.persistRequested(persistAsName, startingTimeText, endingTimeText);
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
        Cookies.setCookie(PrefKey.AllowStrings, params.getIgnoreWords().getText());
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
                    params.getEndingTimeRendered().getText());
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
                        params.getEndingTimeRendered().getText());
            }
        });

		persistButton.setAccessKey('p');
		persistButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                firePersistRequested(
                        persistAsName.getText(),
                        params.getStartingTimeRendered().getText(),
                        params.getEndingTimeRendered().getText());
            }
        });

        ScrollPanel sp = new ScrollPanel(resultPanel);
        //sp.setHeight("20em");
        //sp.setWidth("40em");

		// TODO: use svc root, not hard-coded.
		final String fun = "/items?download=timeslice-raw.dat&mediatypeoverride=text/plain";
		final Anchor downloadLink = new Anchor("Raw-data download", true, fun);
		final CheckBox remote = new CheckBox("Remote drop also.", true);
		remote.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                if (remote.getValue())
                {
                    downloadLink.setHref(fun + "&snapshot=from-web");
                }
                else
                {
                    downloadLink.setHref(fun);
                }
            }
        });

		HorizontalPanel downloadPanel = new HorizontalPanel();
		downloadPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		downloadPanel.add(downloadLink);
		downloadPanel.add(remote);

		HorizontalPanel pp = new HorizontalPanel();
        pp.add(persistButton);
        pp.add(persistAsName);
        pp.add(persisted);

		DisclosurePanel pdp = new DisclosurePanel("Persist");
		pdp.add(pp);

		persistAsName.setText("");
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(refreshButton);
		buttonPanel.add(pdp);

		DockLayoutPanel dp = new DockLayoutPanel(Unit.EM);
		dp.addNorth(params, 15); // why do we have to guess our child's size?
		dp.addNorth(buttonPanel, 4);
		dp.addSouth(downloadPanel, 3);
		dp.addEast(chartBit, 20); // really want this one in pixels! now it's DPI-dependent
		dp.add(sp);

		readPrefs();

		initWidget(dp);
	}

    public void updateChart(List<TaskTotal> items)
	{
	    items = filterItems(items);

		StringBuilder dataPointsString = new StringBuilder();
		StringBuilder labelsString = new StringBuilder();

		Double total = calcTotal(items);

		boolean notTheFirst = false;
		for (TaskTotal item: items)
		{
			if (notTheFirst)
			{
				dataPointsString.append(",");
				labelsString.append("|");
			}

			dataPointsString.append(item.getDurationMillis() / total);
			labelsString.append(item.getWhat().hashCode());

			notTheFirst = true;
		}

		String chartImageUrl = new StringBuilder()
			.append("http://chart.apis.google.com/chart?cht=p3&chd=t:")
			.append(dataPointsString.toString())
			.append("&chs=250x100&chl=")
			.append(labelsString.toString())
			.toString();

		chartBit.clear();
		chartBit.add(new HTML("<h2>Nifty Chart</h2><img src=\"" + chartImageUrl + "\" />"));
		//chartBit.add(new Label(chartImageUrl));
	}

	private List<TaskTotal> filterItems(List<TaskTotal> items)
	{
	    ArrayList<TaskTotal> result = new ArrayList<TaskTotal>();

        String[] allowWords = params.getAllowWords().getText().split(",");
        boolean allowfilter = allowWords.length > 0;
        String[] ignoreWords = params.getIgnoreWords().getText().split(",");
        for (TaskTotal item: items)
        {
            if (allowfilter)
            {
                boolean foundatleastone = false;
                for (String allowWord: allowWords)
                {
                    if (item.getWhat().contains(allowWord))
                    {
                        foundatleastone = true;
                        break;
                    }
                }
                if (!foundatleastone) continue;
            }

            boolean shouldIgnore = false;
            for (String ignoreWord: ignoreWords)
            {
                if (!ignoreWord.isEmpty() && item.getWhat().contains(ignoreWord))
                {
                    shouldIgnore = true;
                    break;
                }
            }
            if (shouldIgnore) continue;

            result.add(item);
        }

        return result;
	}

	private Double calcTotal(List<TaskTotal> items)
	{
		Double total = 0.;
		for (TaskTotal item: filterItems(items))
		{
			total += item.getDurationMillis().intValue();
		}
		return total;
	}

	public void updateResults(List<TaskTotal> items)
	{
		Collections.sort(items, Collections.reverseOrder(new Comparator<TaskTotal>()
				{
					public int compare(TaskTotal o1, TaskTotal o2)
					{
						return o1.getDurationMillis().compareTo(o2.getDurationMillis());
					}
				}));


		FlexTable ft = new FlexTable();
		ft.setCellSpacing(5);
		int row = 0;
		int col = 0;

		ft.setWidget(row, col++, new HTML("<b><u>Who</u></b>", false));
		ft.setWidget(row, col++, new HTML("<b><u>Hours</u></b>", false));
		ft.setWidget(row, col++, new HTML("<b><u>%</u></b>", false));
		ft.setWidget(row, col++, new HTML("<b><u>What</u></b>", false));
		ft.setWidget(row, col++, new HTML("<b><u>Code</u></b>", false));

		row++;

		Double total = calcTotal(items);

		for (TaskTotal item: filterItems(items))
		{
			col = 0;

			ft.setText(row, col++, item.getWho());
			ft.setText(row, col++, NumberFormat.getDecimalFormat().format(item.getDurationMillis() / 1000. / 60. / 60.));
			ft.setText(row, col++, NumberFormat.getPercentFormat().format(item.getDurationMillis() / total));
			ft.setText(row, col++, item.getWhat());
			ft.setText(row, col++, "" + item.getWhat().hashCode());

			row++;
		}

		resultPanel.clear();
		resultPanel.add(ft);
	}

	public void setPersisted(String persistedName)
	{
	    persisted.setText(persistedName);
	}
}
