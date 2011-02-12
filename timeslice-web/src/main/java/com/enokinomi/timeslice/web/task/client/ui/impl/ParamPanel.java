package com.enokinomi.timeslice.web.task.client.ui.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.enokinomi.timeslice.web.task.client.ui.api.IParamChangedListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.inject.Inject;

public class ParamPanel extends Composite implements IParamPanel
{
    private static ParamPanelUiBinder uiBinder = GWT.create(ParamPanelUiBinder.class);
    interface ParamPanelUiBinder extends UiBinder<Widget, ParamPanel> { }

    @UiField protected DateControlBox dateBox;
    @UiField protected DateControlBox startingTime;
    @UiField protected DateControlBox endingTime;
    @UiField protected TextBox allowWords;
    @UiField protected TextBox ignoreWords;

    @Override
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
        for (IParamChangedListener listener: listeners)
        {
            listener.paramChanged(this);
        }
    }

    // used only internally and to service, so tz doesn't matter.
    private static final DateTimeFormat MachineFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

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
        return dateBox.getText();
    }

    @Inject
    ParamPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));
        configure();
    }

    public void configure()
    {
        dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd")));
        startingTime.addValueChangeHandler(defaultChangeHandler);
        startingTime.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd H:m:s.S Z")));
        endingTime.addValueChangeHandler(defaultChangeHandler);
        endingTime.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd H:m:s.S Z")));
        ignoreWords.addChangeHandler(commonChangeHandler);
        allowWords.addChangeHandler(commonChangeHandler);

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
    }

}
