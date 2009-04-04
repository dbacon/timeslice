package bacond.timeslice.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bacond.timeslice.web.gwt.client.util.Checks;
import bacond.timeslice.web.gwt.client.util.IReadableValue;
import bacond.timeslice.web.gwt.client.util.IWritableValue;
import bacond.timeslice.web.gwt.client.util.ValueUtil;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ParamPanel extends Composite
{
	// 2009-03-21T13:30:42.626 -- assume +9
	public static final DateTimeFormat MachineFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+09:00'");

	public static final DateTimeFormat HumanFormat = DateTimeFormat.getFormat("yyyy/MM/dd HH:mm:ss");	

	private final ListBox mediaTypeSelector = new ListBox(false);
	private final TextBox startingTime = new TextBox();
	private final Label startingTimeRendered = new Label("", false);
	private final Label startingTimeError = new Label("", false);
	private final TextBox endingTime = new TextBox();
	private final Label endingTimeRendered = new Label("", false);
	private final Label endingTimeError = new Label("", false);
	private final ListBox processingTypeSelector = new ListBox(false);
	private final Label lastUpdatedLabel = new Label("never");
	
	public static interface IParamChangedListener
	{
		void paramChanged(ParamPanel source);
	}
	
	private final List<IParamChangedListener> listeners = new ArrayList<IParamChangedListener>();
	
	public void addParamChangedListener(IParamChangedListener listener)
	{
		if (null != listener)
		{
			listeners.add(listener);
		}
	}
	
	public void removeParamChangedListener(IParamChangedListener listener)
	{
		listeners.remove(listener);
	}
	
	protected void fireParamChanged()
	{
		for (IParamChangedListener listener: listeners)
		{
			listener.paramChanged(this);
		}
	}
	
	public Label getStartingTimeError()
	{
		return startingTimeError;
	}

	public Label getEndingTimeError()
	{
		return endingTimeError;
	}

	public Label getStartingTimeRendered()
	{
		return startingTimeRendered;
	}

	public Label getEndingTimeRendered()
	{
		return endingTimeRendered;
	}

	public String getSelectedProcessingType()
	{
		return getProcessingTypeSelector().getValue(getProcessingTypeSelector().getSelectedIndex());
	}
	
	public String getSelectedMediaType()
	{
		return getMediaTypeSelector().getValue(getMediaTypeSelector().getSelectedIndex());
	}
	
	public String getSelectedStartingTime()
	{
		return getStartingTime().getText();
	}
	
	public String getSelectedEndingTime()
	{
		return getEndingTime().getText();
	}
	

	public Label getLastUpdatedLabel()
	{
		return lastUpdatedLabel;
	}

	public ListBox getMediaTypeSelector()
	{
		return mediaTypeSelector;
	}

	public TextBox getStartingTime()
	{
		return startingTime;
	}

	public TextBox getEndingTime()
	{
		return endingTime;
	}

	public ListBox getProcessingTypeSelector()
	{
		return processingTypeSelector;
	}
	
	public boolean hasError()
	{
		return false
			|| (!Checks.mapNullTo(getStartingTimeError().getText(), "").trim().isEmpty())
			|| (!Checks.mapNullTo(getEndingTimeError().getText(), "").trim().isEmpty());
	}
	
	public void update()
	{
		lastUpdatedLabel.setText("" + new Date().toString());

		tryParseTimeWithWidgets(endingTime, endingTimeRendered, endingTimeError);
		tryParseTimeWithWidgets(startingTime, startingTimeRendered, startingTimeError);
		
		if (!hasError())
		{
			fireParamChanged();
		}
	}

	private void tryParseTimeWithWidgets(TextBox input, Label result, Label errorMsg)
	{
		tryParseTime(
				ValueUtil.asReadableWritable(input),
				ValueUtil.asReadableWritable(result),
				ValueUtil.asReadableWritable(errorMsg));
	}
	
	private void tryParseTime(IReadableValue<String> readable, IWritableValue<String> result, IWritableValue<String> errorMessage)
	{
		try
		{
			result.setValue(MachineFormat.format(HumanFormat.parse(readable.getValue())));
			errorMessage.setValue(null);
		}
		catch (Exception e)
		{
			result.setValue(null);
			errorMessage.setValue("Parse failed: " + e.getMessage());
		}
	}

	private final ChangeListener commonChangeListener = new ChangeListener()
	{
		public void onChange(Widget arg0)
		{
			update();
		}
	};
	
	public ParamPanel()
	{
		processingTypeSelector.addItem("None", "none");
		processingTypeSelector.addItem("Sum by Task", "sumbydesc");
		processingTypeSelector.addChangeListener(commonChangeListener);

		mediaTypeSelector.addItem("Plain text", "text/plain");
		mediaTypeSelector.addItem("JSON", "application/json");
		mediaTypeSelector.addChangeListener(commonChangeListener);
		
		startingTime.addChangeListener(commonChangeListener);

		endingTime.addChangeListener(commonChangeListener);

		FlexTable table = new FlexTable();
		table.setCellSpacing(3);
		int row = 0;
		
		table.setWidget(row,   0, new Label("Starting"));
		table.setWidget(row,   1, getStartingTime());
		table.setWidget(row,   2, getStartingTimeRendered());
		table.setWidget(row++, 3, getStartingTimeError());
		table.setWidget(row,   0, new Label("Ending"));
		table.setWidget(row,   1, getEndingTime());
		table.setWidget(row,   2, getEndingTimeRendered());
		table.setWidget(row++, 3, getEndingTimeError());
		table.setWidget(row,   0, new Label("Processing:"));
		table.setWidget(row++, 1, processingTypeSelector);
		table.setWidget(row,   0, new Label("Output:"));
		table.setWidget(row++, 1, mediaTypeSelector);
		
		HorizontalPanel lastUpdatedPanel = new HorizontalPanel();
		lastUpdatedPanel.setSpacing(3);
		lastUpdatedPanel.add(new Label("Last updated:"));
		lastUpdatedPanel.add(lastUpdatedLabel);
	
		VerticalPanel vp = new VerticalPanel();
		vp.add(table);
		vp.add(lastUpdatedPanel);
		
		initWidget(vp);

		startingTime.setText(HumanFormat.format(new Date()));
		endingTime.setText(HumanFormat.format(new Date()));

		update();
	}
}
