package com.enokinomi.timeslice.web.task.client.ui_one.impl;

import static com.enokinomi.timeslice.web.core.client.util.TransformUtils.tr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.util.AsyncResult;
import com.enokinomi.timeslice.web.core.client.util.ITransform;
import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.enokinomi.timeslice.web.task.client.controller.api.IControllerListener;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;
import com.enokinomi.timeslice.web.task.client.ui.api.IHistoryPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IHistoryPanelListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IHotlistPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IHotlistPanelListener;
import com.enokinomi.timeslice.web.task.client.ui.impl.DateControlBox;
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

public class InputPanel extends ResizeComposite implements IsWidget
{
    private static InputPanelUiBinder uiBinder = GWT.create(InputPanelUiBinder.class);
    interface InputPanelUiBinder extends UiBinder<Widget, InputPanel> { }

    @UiField protected Anchor updateLink;
    @UiField protected Anchor addHotlink;
    @UiField protected Anchor enterLink;
    @UiField protected RadioButton modeRadioSpecify;
    @UiField protected RadioButton modeRadioNormal;
    @UiField protected DateControlBox specifiedDateBox;
    @UiField protected IHistoryPanel historyPanel;
    @UiField(provided=true) protected SuggestBox taskDescriptionEntry;
    @UiField protected HorizontalPanel entryPanel;
    @UiField protected IHotlistPanel hotlistPanel;
    @UiField protected VerticalPanel actionPanel;
    @UiField protected VerticalPanel idleActionPanel;

    private final MultiWordSuggestOracle suggestSource = new MultiWordSuggestOracle();

    private static class Options
    {
        public boolean isControlSpaceSends() { return false; }
        public int getMaxSize() { return 40; }
        public int getMaxSeconds() { return 60*60*24; }
    }

    private final Options options = new Options();


    public static void bind(final InputPanel ui, final IController controller)
    {
        // TODO: attach to login session
//        loginSupport.addLoginListener(new LoginListener()
//        {
//            @Override
//            public void sessionEnded(boolean retry)
//            {
//                inputPanel.clear();
//            }
//
//            @Override
//            public void newSessionStarted()
//            {
//                // TODO: tell presenter, not UI now.
////                inputPanel.refresh();
//            }
//        });

        ui.addListener(new InputListener()
        {

            @Override
            public void refreshRequested(int maxItems, String starting, String ending)
            {
                controller.startRefreshItems(maxItems, starting, ending);
            }

            @Override
            public void editTagRequested(StartTag editedStartTag)
            {
                controller.startEditDescription(editedStartTag);
            }

            @Override
            public void addTagRequested(String instantString, String description)
            {
                controller.startAddItem(instantString, description);
            }

        });

        controller.addControllerListener(new IControllerListener()
        {
            @Override
            public void serverInfoRecieved(String info)
            {
            }

            @Override
            public void onRefreshTotalsDone(AsyncResult<List<TaskTotal>> result)
            {
            }

            @Override
            public void onRefreshTotalsAssignedDone(AsyncResult<List<AssignedTaskTotal>> result)
            {
            }

            @Override
            public void onRefreshItemsDone(AsyncResult<List<StartTag>> result)
            {
                if (!result.isError())
                {
                    ui.itemsRefreshed(result.getReturned());
                }
                else
                {
                    GWT.log("TODO: show error message: " + result.getThrown().getMessage());
//                    showError(result);
                }
            }

            @Override
            public void onPerformJobDone(AsyncResult<AppJobCompletion> asyncResult)
            {
            }

            @Override
            public void onListAvailableJobsDone(AsyncResult<List<String>> result)
            {
            }

            @Override
            public void onBranded(AsyncResult<BrandInfo> result)
            {
            }

            @Override
            public void onAssignBilleeDone(AsyncResult<Void> result)
            {
            }

            @Override
            public void onAllBilleesDone(AsyncResult<List<String>> asyncResult)
            {
            }

            @Override
            public void onAddItemDone(AsyncResult<Void> result)
            {
                if (!result.isError())
                {
                    //messagePanel.add(new AcknowledgableMessagePanel("Item added."));
                    ui.itemAdded();
                }
                else
                {
                    GWT.log("TODO: show error");
                }
            }
        });

    }


    @Inject
    InputPanel()
    {
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

        historyPanel.addHistoryPanelListener(new IHistoryPanelListener()
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
                scheduleHotlistValidation();
            }
        });

        specifiedDateBox.setValue(new Date());
        specifiedDateBox.addValueChangeHandler(new ValueChangeHandler<Date>()
                {
                    @Override
                    public void onValueChange(ValueChangeEvent<Date> event)
                    {
                        scheduleRefresh();
                    }
                });



        modeRadioSpecify.addValueChangeHandler(new ValueChangeHandler<Boolean>()
                {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event)
                    {
                        fixSpecifiedDateBox(event.getValue());
                        setEntryVisible(!event.getValue());
                        scheduleRefresh();
                    }
                });

        modeRadioNormal.addValueChangeHandler(new ValueChangeHandler<Boolean>()
                {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event)
                    {
                        fixSpecifiedDateBox(!event.getValue());
                        setEntryVisible(event.getValue());
                        scheduleRefresh();
                    }
                });
        modeRadioNormal.setValue(true, true);

//        HorizontalPanel modePanel = new HorizontalPanel();
//        modePanel.setSpacing(5);
//        modePanel.add(new Label(constants.mode()));
//        modePanel.add(modeRadioNormal);
//        modePanel.add(modeRadioSpecify);
//        modePanel.add(specifiedDateBox);

        updateLink.setAccessKey('u');
        updateLink.setHref("#");
        updateLink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                scheduleRefresh();
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
                    scheduleHotlistValidation();
                    scheduleHotlinkValidation();
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
                scheduleHotlinkValidation();

                if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
                {
                    taskDescriptionEntry.setText("");
                }
                else if (event.getNativeEvent().getCtrlKey() && (event.getNativeKeyCode() == KeyCodes.KEY_ENTER))
                {
                    enterNewStartTag(taskDescriptionEntry.getText());
                }
                else if (options.isControlSpaceSends() &&
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

        scheduleHotlistValidation();
        scheduleHotlinkValidation();
    }

    private void setEntryVisible(boolean visible)
    {
        hotlistPanel.asWidget().setVisible(visible);
        entryPanel.setVisible(visible);
    }

    private void scheduleHotlinkValidation()
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                boolean descriptionIsEmpty = taskDescriptionEntry.getText().trim().isEmpty();
                actionPanel.setVisible(!descriptionIsEmpty);
                idleActionPanel.setVisible(descriptionIsEmpty);
            }
        });
    }

    private void scheduleHotlistValidation()
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {

            @Override
            public void execute()
            {
                hotlistPanel.asWidget().setVisible(0 < hotlistPanel.getHotlistItemCount());
            }
        });
    }

    private void fixSpecifiedDateBox(boolean value)
    {
        specifiedDateBox.setEnabled(value);
    }

    // used only internally and to service, so tz doesnt matter.
    public static final DateTimeFormat MachineFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");

    private void scheduleRefresh()
    {
        String starting = MachineFormat.format(new Date(new Date().getTime() - options.getMaxSeconds() * 1000));
        String ending = null;

        if (modeRadioSpecify.getValue() && null != specifiedDateBox.getValue())
        {
            Date specifiedDate = specifiedDateBox.getValue();
            Date beginningOfSpecifiedDay = floorDate(specifiedDate);
            Date untilEndOfSpecifiedDay = new Date(beginningOfSpecifiedDay.getTime() + 1000*3600*24);

            starting = MachineFormat.format(beginningOfSpecifiedDay);
            ending = MachineFormat.format(untilEndOfSpecifiedDay);
        }

        fireRefreshRequested(options.getMaxSize(), starting, ending);
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

    private List<InputListener> listeners = new ArrayList<InputPanel.InputListener>();

    public void addListener(InputListener listener)
    {
        if (listener != null) listeners.add(listener);
    }

    protected void fireEditTagRequested(StartTag editedStartTag)
    {
        for (InputListener l: listeners) l.editTagRequested(editedStartTag);
    }
    protected void fireAddTagRequested(String instantString, String description)
    {
        for (InputListener l: listeners) l.addTagRequested(instantString, description);
    }
    protected void fireRefreshRequested(int maxItems, String starting, String ending)
    {
        for (InputListener l: listeners) l.refreshRequested(maxItems, starting, ending);
    }

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
            scheduleRefresh();
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
    }

    private void updateSuggestSource(List<String> items)
    {
        suggestSource.clear();
        suggestSource.addAll(items);
    }

    public void itemAdded()
    {
        taskDescriptionEntry.setText("");
        scheduleRefresh();
        scheduleHotlinkValidation();
    }

}
