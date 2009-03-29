package bacond.timeslice.web.gwt.client.widget;

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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportPanel extends Composite
{
	private final ParamPanel params = new ParamPanel();
	private final VerticalPanel resultPanel = new VerticalPanel();
	private final Button refreshButton = new Button("Refresh");
	private final VerticalPanel chartBit = new VerticalPanel();
	
	private final Controller controller;
	
	public ReportPanel(Controller controller)
	{
		this.controller = controller;
		
		params.addParamChangedListener(new IParamChangedListener()
		{
			public void paramChanged(ParamPanel source)
			{
				reselectData();
			}
		});
		
		refreshButton.addClickListener(new ClickListener()
		{
			public void onClick(Widget arg0)
			{
				reselectData();
			}
		});

		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(5);
		hp.add(resultPanel);
		hp.add(chartBit);
		
		VerticalPanel vp = new VerticalPanel();
		vp.add(params);
		vp.add(refreshButton);
		vp.add(hp);
		initWidget(vp);
	}
	
	public void updateChart(List<TaskTotal> items)
	{
		StringBuilder dataPointsString = new StringBuilder();
		StringBuilder labelsString = new StringBuilder();

		Double total = 0.;
		for (TaskTotal item: items)
		{
			total += item.getDurationMillis().intValue();
		}

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

	public Controller getController()
	{
		return controller;
	}

	protected void updateResults(List<TaskTotal> items)
	{
		FlexTable ft = new FlexTable();
		ft.setCellSpacing(5);
		int row = 0;
		int col = 0;

		ft.setWidget(row, col++, new HTML("<b><u>Who</u></b>", false));
		ft.setWidget(row, col++, new HTML("<b><u>Hours</u></b>", false));
		ft.setWidget(row, col++, new HTML("<b><u>What</u></b>", false));
		ft.setWidget(row, col++, new HTML("<b><u>Code</u></b>", false));
		
		row++;
		
		for (TaskTotal item: items)
		{
			col = 0;
			
			ft.setText(row, col++, item.getWho());
			ft.setText(row, col++, NumberFormat.getDecimalFormat().format(item.getDurationMillis() / 1000. / 60. / 60.));
			ft.setText(row, col++, item.getWhat());
			ft.setText(row, col++, "" + item.getWhat().hashCode());
			
			row++;
		}
		
		resultPanel.clear();
		resultPanel.add(ft);
	}
	
	protected void reselectData()
	{
		// re-request a list, and set the items on a display panel.
		ItemJsonSvc itemSvc = controller.getItemSvc();
		
		if (null != itemSvc)
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
