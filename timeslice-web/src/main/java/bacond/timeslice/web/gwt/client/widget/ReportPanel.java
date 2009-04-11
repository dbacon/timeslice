package bacond.timeslice.web.gwt.client.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bacond.timeslice.web.gwt.client.beans.TaskTotal;
import bacond.timeslice.web.gwt.client.controller.Controller;
import bacond.timeslice.web.gwt.client.entry.AsyncResult;
import bacond.timeslice.web.gwt.client.server.IRequestEnder;
import bacond.timeslice.web.gwt.client.server.ItemJsonSvc;
import bacond.timeslice.web.gwt.client.server.ProcType;
import bacond.timeslice.web.gwt.client.server.SortDir;
import bacond.timeslice.web.gwt.client.server.TaskTotalFromJson;
import bacond.timeslice.web.gwt.client.widget.ParamPanel.IParamChangedListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportPanel extends Composite
{
	private final ParamPanel params = new ParamPanel();
	private final VerticalPanel resultPanel = new VerticalPanel();
	private final Button refreshButton = new Button("Refresh");
	private final VerticalPanel chartBit = new VerticalPanel();
	private final TextBox ignoreWords = new TextBox();
	
	private final Controller controller;
	
	private static class PrefKey
	{
		public static final String Starting = "timeslice.report.params.starting";
		public static final String Ending = "timeslice.report.params.ending";
		public static final String IgnoreStrings = "timeslice.report.ignorestrings";
	}
	
	private void readPrefs()
	{
		params.getStartingTime().setText(Cookies.getCookie(PrefKey.Starting));
		params.getEndingTime().setText(Cookies.getCookie(PrefKey.Ending));
		ignoreWords.setText(Cookies.getCookie(PrefKey.IgnoreStrings));
		
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
		Cookies.setCookie(PrefKey.IgnoreStrings, ignoreWords.getText());
	}
	
	public ReportPanel(Controller controller)
	{
		this.controller = controller;
		
		params.addParamChangedListener(new IParamChangedListener()
		{
			public void paramChanged(ParamPanel source)
			{
				writePrefs();
				reselectData();
			}
		});
		
		refreshButton.setAccessKey('r');
		refreshButton.addClickListener(new ClickListener()
		{
			public void onClick(Widget arg0)
			{
				reselectData();
			}
		});

		ScrollPanel sp = new ScrollPanel(resultPanel);
		sp.setHeight("20em");
		sp.setWidth("40em");

		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(5);
		hp.add(sp);
		hp.add(chartBit);
		
		HorizontalPanel ignoreHp = new HorizontalPanel();
		ignoreHp.setTitle("Comma-separated list of strings, items \ncontaining any of which will be ignored.");
		ignoreHp.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		ignoreHp.add(new Label("Ignore items containing:"));
		ignoreHp.add(ignoreWords);

		// TODO: use svc root, not hard-coded.
		final String fun = "/items?download=timeslice-raw.dat&mediatypeoverride=text/plain";
		final Anchor downloadLink = new Anchor("Raw-data download", true, fun);
		final CheckBox remote = new CheckBox("Remote drop also.", true);
		remote.addClickListener(new ClickListener()
		{
			public void onClick(Widget arg0)
			{
				if (remote.isChecked())
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

		VerticalPanel vp = new VerticalPanel();
		vp.add(params);
		vp.add(ignoreHp);
		vp.add(refreshButton);
		vp.add(hp);
		vp.add(downloadPanel);
		
		readPrefs();
		
		initWidget(vp);
	}
	
	public void updateChart(List<TaskTotal> items)
	{
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

	private Double calcTotal(List<TaskTotal> items)
	{
		Double total = 0.;
		for (TaskTotal item: items)
		{
			boolean shouldIgnore = false;
			for (String ignoreWord: ignoreWords.getText().split(","))
			{
				if (!ignoreWord.isEmpty() && item.getWhat().contains(ignoreWord))
				{
					shouldIgnore = true;
					break;
				}
			}

			if (!shouldIgnore)
			{
				total += item.getDurationMillis().intValue();
			}
		}
		return total;
	}

	public Controller getController()
	{
		return controller;
	}

	protected void updateResults(List<TaskTotal> items)
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
		
		for (TaskTotal item: items)
		{
			boolean shouldIgnore = false;
			for (String ignoreWord: ignoreWords.getText().split(","))
			{
				if (!ignoreWord.isEmpty() && item.getWhat().contains(ignoreWord))
				{
					shouldIgnore = true;
					break;
				}
			}
			
			if (!shouldIgnore)
			{
				col = 0;

				ft.setText(row, col++, item.getWho());
				ft.setText(row, col++, NumberFormat.getDecimalFormat().format(item.getDurationMillis() / 1000. / 60. / 60.));
				ft.setText(row, col++, NumberFormat.getPercentFormat().format(item.getDurationMillis() / total));
				ft.setText(row, col++, item.getWhat());
				ft.setText(row, col++, "" + item.getWhat().hashCode());

				row++;
			}
		}
		
		resultPanel.clear();
		resultPanel.add(ft);
	}
	
	protected void reselectData()
	{
		// re-request a list, and set the items on a display panel.
		ItemJsonSvc itemSvc = controller.getItemSvc();
		
		if (null != itemSvc && !itemSvc.dontBother())
		{
			itemSvc.beginRefreshItems(
					1000,
					SortDir.desc,
					ProcType.sumbydesc, //ProcType.valueOf(params.getSelectedProcessingType()),
					URL.encodeComponent(params.getStartingTimeRendered().getText()),
					URL.encodeComponent(params.getEndingTimeRendered().getText()),
					new TaskTotalFromJson(),
					new IRequestEnder<List<TaskTotal>>()
					{
						public void end(AsyncResult<List<TaskTotal>> result)
						{
							if (result.isError())
							{
								GWT.log("got error back: " + result.getStatus() + "; " + result.getThrown().getMessage(), result.getThrown());
							}
							else
							{
								updateResults(result.getReturned());
								updateChart(result.getReturned());
							}
						}
					});
		}
	}
}
