package com.enokinomi.timeslice.web.task.client.ui_one.impl;

import static com.enokinomi.timeslice.web.core.client.util.TransformUtils.tr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.ITransform;
import com.enokinomi.timeslice.web.task.client.controller.api.IController;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.ui.api.IHistoryPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IHistoryPanelListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IHotlistPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IHotlistPanelListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IIsWidget;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsListener;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsPanel;
import com.enokinomi.timeslice.web.task.client.ui.api.IOptionsProvider;
import com.enokinomi.timeslice.web.task.client.ui.api.IParamPanel;
import com.enokinomi.timeslice.web.task.client.ui_one.api.BulkItemListener;
import com.enokinomi.timeslice.web.task.client.ui_one.api.IImportBulkItemsDialog;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class InputPanel extends ResizeComposite implements IIsWidget
{
    private final TimesliceAppConstants constants;
    private final DateBox specifiedDateBox = new DateBox();
    private final RadioButton modeRadioSpecify;
    private final RadioButton modeRadioNormal;
    private final IHistoryPanel historyPanel;
    private final MultiWordSuggestOracle suggestSource;
    private final SuggestBox taskDescriptionEntry;
    private final VerticalPanel actionPanel = new VerticalPanel();
    private final VerticalPanel idleActionPanel = new VerticalPanel();
    private final IHotlistPanel hotlistPanel;
    private final Provider<IImportBulkItemsDialog> importBulkItemsDialog;
    private final Timer autoRefreshTimer;
    private final IOptionsProvider options;
    private final IController controller;

    @Override
    public Widget asWidget()
    {
        return this;
    }

    @Inject
    InputPanel(TimesliceAppConstants constants, IController controller, IOptionsProvider optionsProvider, IHistoryPanel historyPanel, Provider<IImportBulkItemsDialog> importBulkItemsDialog, IHotlistPanel hotlistPanel)
    {
        this.constants = constants;
        this.controller = controller;
        this.options = optionsProvider;
        this.historyPanel = historyPanel;
        this.importBulkItemsDialog = importBulkItemsDialog;
        this.hotlistPanel = hotlistPanel;

        modeRadioSpecify = new RadioButton("MODE", constants.specifyDate());
        modeRadioNormal = new RadioButton("MODE", constants.current());

        suggestSource = new MultiWordSuggestOracle();
        taskDescriptionEntry = new SuggestBox(suggestSource);

        autoRefreshTimer = new Timer()
        {
            @Override
            public void run()
            {
                scheduleRefresh();
            }
        };

        initContents();
    }

    private void initContents()
    {
        controller.addControllerListener(new ControllerListenerAdapter()
        {
            @Override
            public void authenticated()
            {
                // start auto-refresh
                if (options.isAutoRefresh()) autoRefreshTimer.scheduleRepeating(options.getAutoRefreshMs());
                scheduleRefresh();
            }

            @Override
            public void unauthenticated(boolean retry)
            {
                // notify ? blank stuff ?
                // stop auto-refresh
                autoRefreshTimer.cancel();
                if (retry) scheduleRefresh();
            }

        });

        options.addOptionsListener(new IOptionsListener()
        {
            public void optionsChanged(IOptionsPanel source)
            {
                autoRefreshTimer.cancel();
                if (options.isAutoRefresh())
                {
                    autoRefreshTimer.scheduleRepeating(options.getAutoRefreshMs());
                }

                scheduleRefresh();
            }
        });

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
                autoRefreshTimer.cancel();
            }

            @Override
            public void editModeLeft()
            {
                if (options.isAutoRefresh()) autoRefreshTimer.scheduleRepeating(options.getAutoRefreshMs());
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
                        scheduleRefresh();
                    }
                });

        modeRadioNormal.addValueChangeHandler(new ValueChangeHandler<Boolean>()
                {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event)
                    {
                        fixSpecifiedDateBox(!event.getValue());
                        scheduleRefresh();
                    }
                });
        modeRadioNormal.setValue(true, true);

        FlowPanel modePanel = new FlowPanel();
        modePanel.add(modeRadioNormal);
        modePanel.add(modeRadioSpecify);
        modePanel.add(specifiedDateBox);

        Anchor updateLink = new Anchor(constants.updateLabel());
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

        Anchor enterLink = new Anchor(constants.enter());
        enterLink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                enterNewStartTag(taskDescriptionEntry.getText());
            }
        });

        Anchor addHotlink = new Anchor(constants.addToHotlist());
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

        actionPanel.add(enterLink);
        actionPanel.add(addHotlink);
        actionPanel.setStyleName("ts-actionPanel");

        Anchor bulkLink = new Anchor(constants.bulk());
        bulkLink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                BulkItemListener listener = new BulkItemListener()
                {
                    @Override
                    public void addItems(List<StartTag> items)
                    {
                        controller.startAddItems(items);
                    }
                };

                IImportBulkItemsDialog d = importBulkItemsDialog.get();
                d.addBulkItemListener(listener);
                d.asDialog().show();
                d.removeBulkItemListener(listener);
            }
        });

        idleActionPanel.add(bulkLink);
        idleActionPanel.setStyleName("ts-idlePanel");

        taskDescriptionEntry.setWidth("30em");
        taskDescriptionEntry.setAccessKey('t');
        taskDescriptionEntry.getTextBox().addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                scheduleHotlinkValidation();

                if (KeyCodes.KEY_ESCAPE == event.getCharCode())
                {
                    taskDescriptionEntry.setText("");
                }
                else if (event.isControlKeyDown() && (KeyCodes.KEY_ENTER == event.getCharCode()))
                {
                    enterNewStartTag(taskDescriptionEntry.getText());
                }
                else if (options.isControlSpaceSends() && event.isControlKeyDown() && (' ' == event.getCharCode()))
                {
                    enterNewStartTag(taskDescriptionEntry.getText());
                }
                event.stopPropagation();
            }
        });

        HorizontalPanel entryPanel = new HorizontalPanel();
        entryPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
        entryPanel.setSpacing(5);
        entryPanel.add(updateLink);
        entryPanel.add(new HTML(constants.task(), false));
        entryPanel.add(taskDescriptionEntry);
        entryPanel.add(actionPanel);
        entryPanel.add(idleActionPanel);

        DockLayoutPanel mainEntryPanel = new DockLayoutPanel(Unit.EM);
        mainEntryPanel.addNorth(modePanel, 3);
        mainEntryPanel.addSouth(hotlistPanel.asWidget(), 4);
        mainEntryPanel.addSouth(entryPanel, 3);
        mainEntryPanel.add(historyPanel.asWidget());

        initWidget(mainEntryPanel);

        scheduleHotlistValidation();
        scheduleHotlinkValidation();
        if (options.isAutoRefresh()) autoRefreshTimer.scheduleRepeating(options.getAutoRefreshMs());
        scheduleRefresh();

    }

    private void scheduleHotlinkValidation()
    {
        DeferredCommand.addCommand(new Command()
        {
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
        DeferredCommand.addCommand(new Command()
        {
            public void execute()
            {
                hotlistPanel.asWidget().setVisible(0 < hotlistPanel.getHotlistItemCount());
            }
        });
    }

    private void fixSpecifiedDateBox(boolean value)
    {
        specifiedDateBox.setEnabled(value);
        specifiedDateBox.setVisible(value);
    }

    private void scheduleRefresh()
    {
        DeferredCommand.addCommand(new Command()
        {
            public void execute()
            {
                String starting = IParamPanel.MachineFormat.format(new Date(new Date().getTime() - options.getMaxSeconds() * 1000));
                String ending = null;

                if (modeRadioSpecify.getValue() && null != specifiedDateBox.getValue())
                {
                    Date specifiedDate = specifiedDateBox.getValue();
                    Date beginningOfSpecifiedDay = floorDate(specifiedDate);
                    Date untilEndOfSpecifiedDay = new Date(beginningOfSpecifiedDay.getTime() + 1000*3600*24);

                    starting = IParamPanel.MachineFormat.format(beginningOfSpecifiedDay);
                    ending = IParamPanel.MachineFormat.format(untilEndOfSpecifiedDay);
                }

                controller.startRefreshItems(
                        options.getMaxSize(),
                        starting,
                        ending);
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
        });
    }

    private void updateStartTag(StartTag editedStartTag)
    {
        controller.startEditDescription(editedStartTag);
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
            controller.startAddItem(instantString, description);
        }
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

    private void updateSuggestSource(ArrayList<String> items)
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
