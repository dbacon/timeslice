package com.enokinomi.timeslice.web.task.client.ui.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.enokinomi.timeslice.web.core.client.ui.PrefHelper;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamChangedListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamPanel;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.inject.Inject;

public class ParamPanel extends Composite implements IParamPanel
{
    private final ParamPanelConstants constants;

    private final DateBox dateBox = new DateBox();
    private final DateBox startingTime = new DateBox();
    private final DateBox endingTime = new DateBox();
    private final TextBox allowWords = new TextBox();
    private final TextBox ignoreWords = new TextBox();

    private static class PrefKey
    {
        public static final String IgnoreStrings = "timeslice.report.ignorestrings";
        public static final String AllowStrings = "timeslice.report.allowstrings";
    }

    public Widget asWidget() { return this; }

    private final List<IParamChangedListener> listeners = new ArrayList<IParamChangedListener>();

    @Override
    public void addParamChangedListener(IParamChangedListener listener)
    {
        if (null != listener)
        {
            listeners.add(listener);
        }
    }

    @Override
    public void removeParamChangedListener(IParamChangedListener listener)
    {
        listeners.remove(listener);
    }

    protected void fireParamChanged()
    {
        writePrefs(); // built-in handler

        for (IParamChangedListener listener: listeners)
        {
            listener.paramChanged(this);
        }
    }

    // used only internally and to service, so tz doesn't matter.
    public static final DateTimeFormat MachineFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");

    @Override
    public String getStartingTimeRendered()
    {
        return MachineFormat.format(startingTime.getValue());
    }

    @Override
    public String getEndingTimeRendered()
    {
        return MachineFormat.format(endingTime.getValue());
    }

    @Override
    public TextBox getIgnoreWords()
    {
        return ignoreWords;
    }

    @Override
    public TextBox getAllowWords()
    {
        return allowWords;
    }

    private final ValueChangeHandler<Date> defaultChangeHandler = new ValueChangeHandler<Date>()
    {
        @Override
        public void onValueChange(ValueChangeEvent<Date> event)
        {
            fireParamChanged();
        }
    };

    private final ChangeHandler commonChangeHandler = new ChangeHandler()
    {
        @Override
        public void onChange(ChangeEvent event)
        {
            fireParamChanged();
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

    @Override
    public String getFullDaySelected()
    {
        return dateBox.getTextBox().getText();
    }

    @Inject
    ParamPanel(ParamPanelConstants constants)
    {
        this.constants = constants;

        initWidgets();
    }

    public void initWidgets()
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

                startingTime.setValue(d1, false);
                endingTime.setValue(d2, false);

                fireParamChanged();
            }
        });

        startingTime.addValueChangeHandler(defaultChangeHandler);
        startingTime.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd H:m:s.S Z")));
        HorizontalPanel startingDatePickerPanel = new HorizontalPanel();
        startingDatePickerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        startingDatePickerPanel.add(new Button(constants.dateReverse(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                Date d = startingTime.getValue();
                Date d1 = dateChopTime(d);
                Date d2 = new Date(d1.getTime() - 24 * 1000 * 60 * 60);

                startingTime.setValue(d2, true);
            }
        }));
        startingDatePickerPanel.add(startingTime);
        startingDatePickerPanel.add(new Button(constants.dateForward(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                Date d = startingTime.getValue();
                Date d1 = dateChopTime(d);
                Date d2 = new Date(d1.getTime() + 24 * 1000 * 60 * 60);

                startingTime.setValue(d2, true);
            }
        }));

        endingTime.addValueChangeHandler(defaultChangeHandler);
        endingTime.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd H:m:s.S Z")));
        HorizontalPanel endingDatePickerPanel = new HorizontalPanel();
        endingDatePickerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        endingDatePickerPanel.add(new Button(constants.dateReverse(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                Date d = endingTime.getValue();
                Date d1 = dateChopTime(d);
                Date d2 = new Date(d1.getTime() - 24 * 1000 * 60 * 60);

                endingTime.setValue(d2, true);
            }
        }));
        endingDatePickerPanel.add(endingTime);
        endingDatePickerPanel.add(new Button(constants.dateForward(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                Date d = endingTime.getValue();
                Date d1 = dateChopTime(d);
                Date d2 = new Date(d1.getTime() + 24 * 1000 * 60 * 60);

                endingTime.setValue(d2, true);
            }
        }));



        ignoreWords.addChangeHandler(commonChangeHandler);
        allowWords.addChangeHandler(commonChangeHandler);

        HorizontalPanel datePickerPanel = new HorizontalPanel();
        datePickerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        datePickerPanel.add(new Button(constants.dateReverse(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                dateBox.setValue(new Date(dateBox.getValue().getTime() - 24 * 60 * 60 * 1000), true);
            }
        }));
        datePickerPanel.add(dateBox);
        datePickerPanel.add(new Button(constants.dateForward(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                dateBox.setValue(new Date(dateBox.getValue().getTime() + 24 * 60 * 60 * 1000), true);
            }
        }));

        FlexTable table = new FlexTable();
        table.setCellSpacing(3);
        int row = 0;
        table.setWidget(  row, 0, new Label(constants.forFullDay()));
        table.setWidget(  row, 1, datePickerPanel);

        ++row;

        HorizontalPanel rangeParam = new HorizontalPanel();
        rangeParam.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        rangeParam.add(startingDatePickerPanel);
        rangeParam.add(new Label("through"));
        rangeParam.add(endingDatePickerPanel);

        table.setText(row, 0, "Range");
        table.setWidget(row, 1, rangeParam);
        ++row;

        table.setWidget(row, 0, new Label(constants.allowWordsContaining()));
        table.setWidget(row, 1, allowWords);
        ++row;

        table.setWidget(row, 0, new Label(constants.ignoreWordsContaining()));
        table.setWidget(row, 1, ignoreWords);
        ++row;


        FlowPanel fp = new FlowPanel();
        fp.add(table);

        initWidget(fp);

        dateBox.setValue(new Date(), true);

        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
//                fireParamChanged();
                readPrefs();
            }
        });
    }

    private void readPrefs()
    {
        getIgnoreWords().setText(Cookies.getCookie(PrefKey.IgnoreStrings));
        getAllowWords().setText(Cookies.getCookie(PrefKey.AllowStrings));

        fireParamChanged();
    }

    private void writePrefs()
    {
        Cookies.setCookie(PrefKey.IgnoreStrings, getIgnoreWords().getText(), PrefHelper.createDateSufficientlyInTheFuture());
        Cookies.setCookie(PrefKey.AllowStrings, getAllowWords().getText(), PrefHelper.createDateSufficientlyInTheFuture());
    }
}
