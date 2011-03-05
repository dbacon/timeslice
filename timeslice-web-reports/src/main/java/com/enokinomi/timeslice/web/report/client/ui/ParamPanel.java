package com.enokinomi.timeslice.web.report.client.ui;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.core.client.ui.DateControlBox;
import com.enokinomi.timeslice.web.core.client.util.ListenerManager;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.task.client.presenter.InputPlace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
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
    @UiField protected Anchor itemsForSelectedDateClickable;
    @UiField protected DateControlBox startingTime;
    @UiField protected DateControlBox endingTime;
    @UiField protected TextBox allowWords;
    @UiField protected TextBox ignoreWords;

    @Override
    public Widget asWidget() { return this; }

    private final ListenerManager<IParamChangedListener> listenerMgr = new ListenerManager<IParamChangedListener>();
    @Override public Registration addParamChangedListener(IParamChangedListener listener) { return listenerMgr.addListener(listener); }

    protected void fireParamChanged() { for (IParamChangedListener listener: listenerMgr.getListeners()) listener.paramChanged(this); }
    protected void fireHistoryRequested(Date when) { for (IParamChangedListener listener: listenerMgr.getListeners()) listener.historyRequested(when); }
    protected void fireAllowWordsChanged(String allowWords) { for (IParamChangedListener l: listenerMgr.getListeners()) l.allowWordsChanged(allowWords); }
    protected void fireIgnoreWordsChanged(String ignoreWords) { for (IParamChangedListener l: listenerMgr.getListeners()) l.ignoreWordsChanged(ignoreWords); }

    // used only internally and to service, so tz doesn't matter.
    private static final DateTimeFormat MachineFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");

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
    public String getIgnoreWords()
    {
        return ignoreWords.getText();
    }

    @Override
    public void setIgnoreWords(String ignoreWords, boolean fireEvents)
    {
        this.ignoreWords.setValue(ignoreWords, fireEvents);
    }

    @Override
    public String getAllowWords()
    {
        return allowWords.getText();
    }

    @Override
    public void setAllowWords(String allowWords, boolean fireEvents)
    {
        this.allowWords.setValue(allowWords, fireEvents);
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

    @UiField(provided=true)
    protected final PlaceHistoryMapper placeHistoryMapper;

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

    @Override
    public void setFullDaySelected(Date when, boolean fireEvents)
    {
        if (when == null) when = new Date();

        dateBox.setValue(when, fireEvents);

        writeStartingEndingInTermsOfSelectedDay(when, fireEvents);
    }

    private void writeStartingEndingInTermsOfSelectedDay(Date when, boolean fireEvents)
    {
        Date d = when;
        Date d1 = dateChopTime(d);
        Date d2 = new Date(d1.getTime() + 1000 * 60 * 60 *24);

        startingTime.setValue(d1, false);
        endingTime.setValue(d2, false);

        if (fireEvents)
        {
            fireParamChanged();
        }
    }

    @UiConstructor
    @Inject
    ParamPanel(PlaceHistoryMapper placeHistoryMapper)
    {
        this.placeHistoryMapper = placeHistoryMapper;

        initWidget(uiBinder.createAndBindUi(this));
        configure();
    }

    public void configure()
    {
        itemsForSelectedDateClickable.setHref("#");
        dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd")));
        startingTime.addValueChangeHandler(defaultChangeHandler);
        startingTime.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd H:m:s.S Z")));
        endingTime.addValueChangeHandler(defaultChangeHandler);
        endingTime.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd H:m:s.S Z")));
        ignoreWords.addChangeHandler(commonChangeHandler);
        allowWords.addChangeHandler(commonChangeHandler);

        updateItemsLink(dateBox.getValue());

        dateBox.addValueChangeHandler(new ValueChangeHandler<Date>()
            {
                @Override
                public void onValueChange(ValueChangeEvent<Date> event)
                {
                    writeStartingEndingInTermsOfSelectedDay(event.getValue(), true);

                    updateItemsLink(event.getValue());
                }
            });

        allowWords.addValueChangeHandler(new ValueChangeHandler<String>()
            {
                @Override
                public void onValueChange(ValueChangeEvent<String> event)
                {
                    fireAllowWordsChanged(event.getValue());
                }
            });

        ignoreWords.addValueChangeHandler(new ValueChangeHandler<String>()
            {
                @Override
                public void onValueChange(ValueChangeEvent<String> event)
                {
                    fireIgnoreWordsChanged(event.getValue());
                }
            });

    }

    private void updateItemsLink(Date when)
    {
        itemsForSelectedDateClickable.setHref(
            "#" + placeHistoryMapper.getToken(
                    new InputPlace("report-panel", false, when)));
    }

    @Override
    public void restoreFromSettings(Map<String, List<String>> result)
    {
        if (result.containsKey("ui.params.allowwords"))
        {
            setAllowWords(result.get("ui.params.allowwords").get(0), false);
        }

        if (result.containsKey("ui.params.ignorewords"))
        {
            setIgnoreWords(result.get("ui.params.ignorewords").get(0), false);
        }

        fireParamChanged();
    }
}
