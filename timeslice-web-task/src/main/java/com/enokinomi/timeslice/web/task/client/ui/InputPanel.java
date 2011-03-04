package com.enokinomi.timeslice.web.task.client.ui;

import static com.enokinomi.timeslice.web.core.client.util.TransformUtils.tr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.core.client.ui.DateControlBox;
import com.enokinomi.timeslice.web.core.client.ui.IClearable;
import com.enokinomi.timeslice.web.core.client.ui.Initializable;
import com.enokinomi.timeslice.web.core.client.ui.NavPanel;
import com.enokinomi.timeslice.web.core.client.util.Checks;
import com.enokinomi.timeslice.web.core.client.util.ITransform;
import com.enokinomi.timeslice.web.core.client.util.ListenerManager;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.settings.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.settings.client.ui.impl.OptionsPanel.UiOptionKey;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.presenter.TzSupport;
import com.enokinomi.timeslice.web.task.client.ui.IHotlistPanel.IHotlistPanelListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class InputPanel extends ResizeComposite implements IsWidget, IClearable, Initializable
{
    private static InputPanelUiBinder uiBinder = GWT.create(InputPanelUiBinder.class);
    interface InputPanelUiBinder extends UiBinder<Widget, InputPanel> { }

    @UiField(provided=true) protected NavPanel navPanel;
    @UiField protected Anchor updateLink;
    @UiField protected Anchor addHotlink;
    @UiField protected Anchor enterLink;
    @UiField protected RadioButton modeRadioSpecify;
    @UiField protected RadioButton modeRadioNormal;
    @UiField protected DateControlBox specifiedDateBox;
    @UiField(provided=true) protected IHistoryPanel historyPanel;
    @UiField(provided=true) protected SuggestBox taskDescriptionEntry;
    @UiField protected HorizontalPanel entryPanel;
    @UiField protected IHotlistPanel hotlistPanel;
    @UiField protected VerticalPanel actionPanel;
    @UiField protected VerticalPanel idleActionPanel;

    private final MultiWordSuggestOracle suggestSource = new MultiWordSuggestOracle();

    private static class Options
    {
        public int maxSize = 0;
        public int maxSeconds = 0;
        public boolean controlSpaceSends = false;
        public boolean taskInTitlebar = false;
        public String titlebarTemplate = "[TS] " + IOptionsPanel.CURRENTTASK;
    }

    private Options options = new Options();

    public String originalWindowTitle = "";

    @UiField(provided=true) final TzSupport tzSupport;

    public NavPanel getNavPanel()
    {
        return navPanel;
    }

    @Inject
    InputPanel(@Named("populated") NavPanel navPanel, TzSupport tzSupport, IHistoryPanel historyPanel)
    {
        this.historyPanel = historyPanel;
        this.navPanel = navPanel;
        this.tzSupport = tzSupport;
        taskDescriptionEntry = new SuggestBox(suggestSource);

        initWidget(uiBinder.createAndBindUi(this));

//        modeRadioSpecify = new RadioButton("MODE", constants.specifyDate());
//        modeRadioNormal = new RadioButton("MODE", constants.current());

        initContents();
    }

    private void initContents()
    {
        // TODO: switch to settings listener.
//        options.addOptionsListener(new IOptionsListener()
//        {
//            public void optionsChanged(IOptionsPanel source)
//            {
//                scheduleRefresh();
//            }
//        });

        historyPanel.addListener(new IHistoryPanel.Listener()
        {
            public void interestingThing(String p)
            {
                enterNewStartTag(p);
            }

            public void fireEdited(StartTag editedStartTag)
            {
                updateStartTag(editedStartTag);
            }

            public void fireTimeEdited(StartTag startTag)
            {
                enterNewStartTag(startTag.getInstantString(), startTag.getDescription());
            }

            public void hotlisted(String name, String description)
            {
                hotlistPanel.addAsHotlistItem(name, description);
            }

            @Override
            public void editModeEntered()
            {
            }

            @Override
            public void editModeLeft()
            {
            }
        });

        hotlistPanel.addHotlistPanelListener(new IHotlistPanelListener()
        {
            public void hotlistItemClicked(String description)
            {
                enterNewStartTag(description);
            }

            public void hotlistChanged()
            {
                consistentizeHotlist();
            }
        });

        specifiedDateBox.addValueChangeHandler(new ValueChangeHandler<Date>()
                {
                    @Override
                    public void onValueChange(ValueChangeEvent<Date> event)
                    {
                        scheduleRefresh("due to specified-date value-change");
                    }
                });



        modeRadioSpecify.addValueChangeHandler(new ValueChangeHandler<Boolean>()
                {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event)
                    {
                        if (event.getValue())
                        {
                            fixSpecifiedDateBox(event.getValue());
                            setEntryVisible(!event.getValue());
                            scheduleRefresh("due to 'specify' radio value-change");
                        }
                    }
                });

        modeRadioNormal.addValueChangeHandler(new ValueChangeHandler<Boolean>()
                {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event)
                    {
                        if (event.getValue())
                        {
                            fixSpecifiedDateBox(!event.getValue());
                            setEntryVisible(event.getValue());
                            scheduleRefresh("due to 'normal' radio value-change");
                        }
                    }
                });

//        HorizontalPanel modePanel = new HorizontalPanel();
//        modePanel.setSpacing(5);
//        modePanel.add(new Label(constants.mode()));
//        modePanel.add(modeRadioNormal);
//        modePanel.add(modeRadioSpecify);
//        modePanel.add(specifiedDateBox);

        updateLink.setAccessKey('u');
        updateLink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                scheduleRefresh("due to update-link click-event");
            }
        });

        enterLink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                enterNewStartTag(taskDescriptionEntry.getText());
            }
        });

        addHotlink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                if (!taskDescriptionEntry.getText().trim().isEmpty())
                {
                    hotlistPanel.addAsHotlistItem(taskDescriptionEntry.getText(), taskDescriptionEntry.getText());
                    taskDescriptionEntry.setText("");
                    consistentizeHotlinks();
                    consistentizeHotlist();
                }
            }
        });

//        actionPanel.add(enterLink);
//        actionPanel.add(addHotlink);
//        actionPanel.setStyleName("ts-actionPanel");
//
//        idleActionPanel.setStyleName("ts-idlePanel");

        taskDescriptionEntry.setWidth("30em");
        taskDescriptionEntry.setAccessKey('t');
        taskDescriptionEntry.setAutoSelectEnabled(false);
        taskDescriptionEntry.getTextBox().addKeyDownHandler(new KeyDownHandler()
        {
            @Override
            public void onKeyDown(KeyDownEvent event)
            {
                Scheduler.get().scheduleDeferred(new ScheduledCommand()
                {
                    @Override
                    public void execute()
                    {
                        consistentizeHotlinks();
                    }
                });

                if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
                {
                    taskDescriptionEntry.setText("");
                }
                else if (event.getNativeEvent().getCtrlKey() && (event.getNativeKeyCode() == KeyCodes.KEY_ENTER))
                {
                    enterNewStartTag(taskDescriptionEntry.getText());
                }
                else if (options.controlSpaceSends &&
                        event.getNativeEvent().getCtrlKey() && (event.getNativeKeyCode() == ' '))
                {
                    enterNewStartTag(taskDescriptionEntry.getText());
                }
            }
        });

//        entryPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
//        entryPanel.setSpacing(5);
//        entryPanel.add(updateLink);
//        entryPanel.add(new HTML(constants.task(), false));
//        entryPanel.add(taskDescriptionEntry);
//        entryPanel.add(actionPanel);
//        entryPanel.add(idleActionPanel);
//
//        DockLayoutPanel mainEntryPanel = new DockLayoutPanel(Unit.EM);
//        mainEntryPanel.addNorth(modePanel, 3);
//        mainEntryPanel.addSouth(hotlistPanel.asWidget(), 4);
//        mainEntryPanel.addSouth(entryPanel, 3);
//        mainEntryPanel.add(historyPanel.asWidget());
//
////        initWidget(mainEntryPanel);

    }

    private void setEntryVisible(boolean visible)
    {
        hotlistPanel.asWidget().setVisible(visible);
        entryPanel.setVisible(visible);
    }

    public void setHistoryMode(boolean history, boolean fireEvents)
    {
        modeRadioNormal.setValue(!history, fireEvents);
        modeRadioSpecify.setValue(history, fireEvents);
    }

    public void setHistoricDate(Date when, boolean fireEvents)
    {
        if (when == null) when = new Date();
        specifiedDateBox.setValue(when, fireEvents);
    }

    private void consistentizeHotlinks()
    {
        boolean descriptionIsEmpty = taskDescriptionEntry.getText().trim().isEmpty();
        actionPanel.setVisible(!descriptionIsEmpty);
        idleActionPanel.setVisible(descriptionIsEmpty);
    }

    private void fixSpecifiedDateBox(boolean value)
    {
        specifiedDateBox.setEnabled(value);
    }

    // used only internally and to service, so tz doesnt matter.
    public static final DateTimeFormat MachineFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");


    @Override
    public void initialize(String callerPurpose)
    {
        consistentizeHotlist();
        consistentizeHotlinks();

        scheduleRefresh("due to call to input-panel.initialize, " + callerPurpose);
        getNavPanel().initialize("due to call to input-panel.initialize, " + callerPurpose);
    }

    private void consistentizeHotlist()
    {
        hotlistPanel.asWidget().setVisible(0 < hotlistPanel.getHotlistItemCount());
    }

    private void scheduleRefresh(String callerPurpose)
    {
        String starting = MachineFormat.format(new Date(new Date().getTime() - options.maxSeconds * 1000));
        String ending = null;

        if (modeRadioSpecify.getValue() && null != specifiedDateBox.getValue())
        {
            GWT.log("doing historic refresh (" + callerPurpose + ")");
            Date specifiedDate = specifiedDateBox.getValue();
            Date beginningOfSpecifiedDay = floorDate(specifiedDate);
            Date untilEndOfSpecifiedDay = new Date(beginningOfSpecifiedDay.getTime() + 1000*3600*24);

            starting = MachineFormat.format(beginningOfSpecifiedDay);
            ending = MachineFormat.format(untilEndOfSpecifiedDay);
        }
        else
        {
            GWT.log("doing current refresh (" + callerPurpose + ")");
        }

        fireRefreshRequested(options.maxSize, starting, ending);
    }

    /**
     * Helps when date-pickers return noon, and you want start-of-day 00:00.
     *
     * @param specifiedDate
     * @return
     */
    @SuppressWarnings("deprecation")
    private Date floorDate(Date specifiedDate)
    {
        Date floor = new Date(specifiedDate.getTime());
        floor.setHours(0);
        floor.setMinutes(0);
        floor.setSeconds(0);
        return floor;
    }

    public static interface InputListener
    {
        void editTagRequested(StartTag editedStartTag); // TODO: change to data, not StartTag
        void addTagRequested(String instantString, String description);
        void refreshRequested(int maxItems, String starting, String ending);
    }

    private final ListenerManager<InputListener> listenerMgr = new ListenerManager<InputPanel.InputListener>();
    public Registration addListener(final InputListener listener) { return listenerMgr.addListener(listener); }

    protected void fireEditTagRequested(StartTag editedStartTag) { for (InputListener l: listenerMgr.getListeners()) l.editTagRequested(editedStartTag); }
    protected void fireAddTagRequested(String instantString, String description) { for (InputListener l: listenerMgr.getListeners()) l.addTagRequested(instantString, description); }
    protected void fireRefreshRequested(int maxItems, String starting, String ending) { for (InputListener l: listenerMgr.getListeners()) l.refreshRequested(maxItems, starting, ending); }

    private void updateStartTag(StartTag editedStartTag)
    {
        fireEditTagRequested(editedStartTag);
    }

    private void enterNewStartTag(String description)
    {
        enterNewStartTag(null, description);
    }

    private void enterNewStartTag(String instantString, String description)
    {
        if (description.trim().isEmpty())
        {
            scheduleRefresh("due to enter empty tag");
        }
        else
        {
            fireAddTagRequested(instantString, description);
        }
    }

    public void clear()
    {
        List<String> noWords = Arrays.<String>asList();
        updateSuggestSource(noWords);
        historyPanel.setSuggestWords(noWords);
        historyPanel.clear(true);
    }

    public void itemsRefreshed(List<StartTag> items)
    {
        historyPanel.clear(false);
        historyPanel.addItems(items);

        ArrayList<String> descriptions = tr(items, new ArrayList<String>(), new ITransform<StartTag, String>()
        {
            @Override
            public String apply(StartTag r)
            {
                return r.getDescription();
            }
        });

        updateSuggestSource(descriptions);
        historyPanel.setSuggestWords(descriptions);

        calculateWindowTitle(items);
    }

    private static final StartTag UnknownTag = new StartTag(null, null, null, "-unknown-", false);

    private StartTag findCurrentStartTag(List<StartTag> items)
    {
        // Since we are searching for 'now'
        // among the rendered items in history,
        // we must also use the same TZ which they
        // were rendered in, to make the lexicographical
        // comparison valid.

        String now = tzSupport.renderForClientMachine(new Date());

        for (int i = 0; i < items.size(); ++i)
        {
            if (now.compareTo(items.get(i).getInstantString()) >= 0)
            {
                return items.get(i);
            }
        }

        return null;
    }

    public String renderTitlebar(String currentTaskDescription)
    {
        return options.titlebarTemplate.replaceAll(IOptionsPanel.CURRENTTASK, currentTaskDescription);
    }

    private void calculateWindowTitle(List<StartTag> result)
    {
        if (options.taskInTitlebar)
        {
            Window.setTitle(renderTitlebar(Checks.mapNullTo(findCurrentStartTag(result), UnknownTag).getDescription()));
        }
    }

    private void updateSuggestSource(List<String> items)
    {
        suggestSource.clear();
        suggestSource.addAll(items);
    }

    public void itemAdded()
    {
        taskDescriptionEntry.setText("");
        scheduleRefresh("due to item added");
        consistentizeHotlinks();
    }

    public void handleUserSettings(Map<String, List<String>> result)
    {
        if (result.containsKey(UiOptionKey.ControlSpaceSendsEnabled))
        {
            options.controlSpaceSends = "true".equals(result.get(UiOptionKey.ControlSpaceSendsEnabled).get(0));
        }

        if (result.containsKey(UiOptionKey.MaxSeconds))
        {
            options.maxSeconds = (int) Double.parseDouble(result.get(UiOptionKey.MaxSeconds).get(0));
        }

        if (result.containsKey(UiOptionKey.MaxSize))
        {
            options.maxSize = Integer.parseInt(result.get(UiOptionKey.MaxSize).get(0));
        }

        if (result.containsKey(UiOptionKey.TaskInTitleBarEnabled))
        {
            options.taskInTitlebar = Boolean.parseBoolean(result.get(UiOptionKey.TaskInTitleBarEnabled).get(0));
        }

        if (result.containsKey(UiOptionKey.TaskInTitleBarTemplate))
        {
            options.titlebarTemplate = result.get(UiOptionKey.TaskInTitleBarTemplate).get(0);
        }

    }
}
