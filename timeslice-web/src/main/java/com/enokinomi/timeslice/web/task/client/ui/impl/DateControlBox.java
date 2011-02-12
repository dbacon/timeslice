package com.enokinomi.timeslice.web.task.client.ui.impl;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.Format;

public class DateControlBox extends Composite implements HasValue<Date>, HasValueChangeHandlers<Date>
{
    private static DateControlBoxUiBinder uiBinder = GWT.create(DateControlBoxUiBinder.class);
    interface DateControlBoxUiBinder extends UiBinder<Widget, DateControlBox> { }

    @UiField protected Button prevButton;
    @UiField protected Button nextButton;
    @UiField protected DateBox dateBox;

    public DateControlBox()
    {
        initWidget(uiBinder.createAndBindUi(this));

        dateBox.setValue(new Date());

        dateBox.addValueChangeHandler(new ValueChangeHandler<Date>()
        {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event)
            {
                fireEvent(event);
            }
        });
    }

    public String getText()
    {
        return dateBox.getTextBox().getText();
    }

    public void setFormat(Format format)
    {
        dateBox.setFormat(format);
    }

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

    @UiHandler("prevButton")
    protected void prevClicked(ClickEvent e)
    {
        Date d = dateBox.getValue();
        Date d1 = dateChopTime(d);
        Date d2 = new Date(d1.getTime() - 24 * 1000 * 60 * 60);

        dateBox.setValue(d2, true);
    }

    @UiHandler("nextButton")
    protected void nextClicked(ClickEvent e)
    {
        Date d = dateBox.getValue();
        Date d1 = dateChopTime(d);
        Date d2 = new Date(d1.getTime() + 24 * 1000 * 60 * 60);

        dateBox.setValue(d2, true);
    }

    public void setDate(Date date)
    {
        dateBox.setValue(date);
    }

    public Date getDate()
    {
        return dateBox.getValue();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler)
    {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public Date getValue()
    {
        return dateBox.getValue();
    }

    @Override
    public void setValue(Date value)
    {
        this.dateBox.setValue(value);
    }

    @Override
    public void setValue(Date value, boolean fireEvents)
    {
        this.dateBox.setValue(value, fireEvents);
    }

}
