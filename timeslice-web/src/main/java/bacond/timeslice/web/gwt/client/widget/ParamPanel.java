package bacond.timeslice.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bacond.timeslice.web.gwt.client.util.Checks;
import bacond.timeslice.web.gwt.client.util.IReadableValue;
import bacond.timeslice.web.gwt.client.util.IWritableValue;
import bacond.timeslice.web.gwt.client.util.ValueUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

public class ParamPanel extends Composite
{
	// 2009-03-21T13:30:42.626 -- assume +9
	public static final DateTimeFormat MachineFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+09:00'");

	public static final DateTimeFormat HumanFormat = DateTimeFormat.getFormat("yyyy/MM/dd HH:mm:ss");

	private final DateBox dateBox = new DateBox();
	private final TextBox startingTime = new TextBox();
	private final Label startingTimeRendered = new Label("", false);
	private final Label startingTimeError = new Label("", false);
	private final TextBox endingTime = new TextBox();
	private final Label endingTimeRendered = new Label("", false);
	private final Label endingTimeError = new Label("", false);
	private final Label allowWordsOn = new Label("Allow words containing:");
	private final TextBox allowWords = new TextBox();
	private final Label ignoreWordsOn = new Label("Ignore words containing:");
    private final TextBox ignoreWords = new TextBox();

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

	public String getSelectedStartingTime()
	{
		return getStartingTime().getText();
	}

	public String getSelectedEndingTime()
	{
		return getEndingTime().getText();
	}

	public TextBox getStartingTime()
	{
		return startingTime;
	}

	public TextBox getEndingTime()
	{
		return endingTime;
	}

	public TextBox getIgnoreWords()
    {
        return ignoreWords;
    }

	public TextBox getAllowWords()
	{
	    return allowWords;
	}

    public boolean hasError()
	{
		return false
			|| (!Checks.mapNullTo(getStartingTimeError().getText(), "").trim().isEmpty())
			|| (!Checks.mapNullTo(getEndingTimeError().getText(), "").trim().isEmpty());
	}

	public void update()
	{
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

	private final ChangeHandler commonChangeHandler = new ChangeHandler()
    {
        @Override
        public void onChange(ChangeEvent event)
        {
            update();
        }
    };

    @SuppressWarnings("deprecation")
    private Date dateChopTime(Date date)
    {
        Date d = new Date();
        d.setYear(date.getYear());
        d.setMonth(date.getMonth());
        d.setDate(date.getDate());
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);
        return d;
    }

    public String getFullDaySelected()
    {
        return dateBox.getTextBox().getText();
    }

	public ParamPanel()
	{
	    dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd")));

	    dateBox.addValueChangeHandler(new ValueChangeHandler<Date>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event)
            {
                Date d = event.getValue();
                Date d1 = dateChopTime(d);
                Date d2 = new Date(d1.getTime() + 1000 * 60 * 60 *24);

                GWT.log("selected date: " + d.toString());
                startingTime.setText(HumanFormat.format(d1));
                endingTime.setText(HumanFormat.format(d2));
                update();
            }
        });

		startingTime.addChangeHandler(commonChangeHandler);
		endingTime.addChangeHandler(commonChangeHandler);
		ignoreWords.addChangeHandler(commonChangeHandler);
		allowWords.addChangeHandler(commonChangeHandler);

		FlexTable table = new FlexTable();
		table.setCellSpacing(3);
		int row = 0;

		table.setWidget(  row, 0, new Label("For full-day: "));
		table.setWidget(  row, 1, dateBox);
        table.setWidget(++row, 0, new Label("Starting"));
        table.setWidget(  row, 1, getStartingTime());
        table.setWidget(  row, 2, getStartingTimeRendered());
        table.setWidget(  row, 3, getStartingTimeError());
        table.setWidget(++row, 0, new Label("Ending"));
        table.setWidget(  row, 1, getEndingTime());
        table.setWidget(  row, 2, getEndingTimeRendered());
        table.setWidget(  row, 3, getEndingTimeError());
        table.setWidget(++row, 0, allowWordsOn);
        table.setWidget(  row, 1, allowWords);
        table.setWidget(++row, 0, ignoreWordsOn);
        table.setWidget(  row, 1, ignoreWords);


		FlowPanel fp = new FlowPanel();
		fp.add(table);

		initWidget(fp);

		dateBox.setValue(new Date(), true);

		DeferredCommand.addCommand(new Command()
        {
            @Override
            public void execute()
            {
                update();
            }
        });
	}
}
