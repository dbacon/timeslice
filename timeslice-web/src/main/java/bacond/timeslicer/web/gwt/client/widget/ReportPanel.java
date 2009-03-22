package bacond.timeslicer.web.gwt.client.widget;

import java.util.List;

import bacond.timeslicer.web.gwt.client.beans.StartTag;
import bacond.timeslicer.web.gwt.client.controller.Controller;
import bacond.timeslicer.web.gwt.client.entry.AsyncResult;
import bacond.timeslicer.web.gwt.client.server.IRequestEnder;
import bacond.timeslicer.web.gwt.client.server.ItemJsonSvc;
import bacond.timeslicer.web.gwt.client.server.SortDir;
import bacond.timeslicer.web.gwt.client.server.ItemJsonSvc.ProcType;
import bacond.timeslicer.web.gwt.client.widget.ParamPanel.IParamChangedListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReportPanel extends Composite
{
	private final ParamPanel params = new ParamPanel();
	
	private Controller controller = null;
	
	public ReportPanel()
	{
		params.addParamChangedListener(new IParamChangedListener()
		{
			public void paramChanged(ParamPanel source)
			{
				paramsChanged();
			}
		});

		VerticalPanel vp = new VerticalPanel();
		vp.add(params);
		initWidget(vp);
	}

	public Controller getController()
	{
		return controller;
	}

	public void setController(Controller controller)
	{
		this.controller = controller;
	}

	protected void paramsChanged()
	{
		// re-request a list, and set the items on a display panel.
		ItemJsonSvc itemSvc = controller.getItemSvc();
		
		if (null != itemSvc)
		{
			itemSvc.beginRefreshItems(
					1000,
					SortDir.desc,
					ProcType.valueOf(params.getSelectedProcessingType()),
					params.getStartingTimeRendered().getText(),
					params.getEndingTimeRendered().getText(),
					new IRequestEnder<List<StartTag>>()
					{
						public void end(AsyncResult<List<StartTag>> result)
						{
							if (result.isError())
							{
								GWT.log("got error back: " + result.getStatus() + "; " + result.getThrown().getMessage(), result.getThrown());
							}
							else
							{
								GWT.log("Got report back! " + result.getReturned().size() + " item(s)", null);
							}
						}
					});
		}
	}
}
