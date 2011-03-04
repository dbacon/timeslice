package com.enokinomi.timeslice.web.task.client.ui;

import static com.enokinomi.timeslice.web.task.client.presenter.HumanReadableTimeHelper.formatDuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.enokinomi.timeslice.web.core.client.ui.EditableLabel;
import com.enokinomi.timeslice.web.core.client.util.ListenerManager;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.presenter.TzSupport;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class HistoryPanel extends ResizeComposite implements IHistoryPanel
{
    private static HistoryPanelUiBinder uiBinder = GWT.create(HistoryPanelUiBinder.class);
    interface HistoryPanelUiBinder extends UiBinder<Widget, HistoryPanel> { }

    private final TaskPanelConstants constants = GWT.create(TaskPanelConstants.class);

    @UiField protected FlexTable table;
    @UiField protected ScrollPanel scroller;

    private final List<StartTag> items = new ArrayList<StartTag>();

    private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

    private ListenerManager<Listener> listenerMgr = new ListenerManager<IHistoryPanel.Listener>();
    @Override public void addListener(Listener listener) { listenerMgr.addListener(listener); }

    @Override
    public void removeHistoryPanelListener(Listener listener)
    {
        listenerMgr.getListeners().remove(listener);
    }

    protected void fireEditModeEntered()
    {
        for (Listener listener: listenerMgr.getListeners())
        {
            listener.editModeEntered();
        }
    }

    protected void fireEditModeLeft()
    {
        for (Listener listener: listenerMgr.getListeners())
        {
            listener.editModeLeft();
        }
    }

    protected void fireHotlisted(String name, String description)
    {
        for (Listener listener: listenerMgr.getListeners())
        {
            listener.hotlisted(name, description);
        }
    }

    protected void fireInterestingThing(String p)
    {
        for (Listener listener: listenerMgr.getListeners())
        {
            listener.interestingThing(p);
        }
    }

    protected void fireEdited(StartTag startTag)
    {
        for (Listener listener: listenerMgr.getListeners())
        {
            listener.fireEdited(startTag);
        }
    }

    protected void fireTimeEdited(StartTag startTag)
    {
        for (Listener listener: listenerMgr.getListeners())
        {
            listener.fireTimeEdited(startTag);
        }
    }

    @UiConstructor
    @Inject
    HistoryPanel(TzSupport tzSupport)
    {
        this.tzSupport = tzSupport;

        initWidget(uiBinder.createAndBindUi(this));

        // no way to do this in the ui-binder?
        table.setWidth("100%");
        table.getColumnFormatter().setWidth(2, "10em");
        table.getColumnFormatter().addStyleName(1, "tsTimeField");
    }

    @Override
    public void clear(boolean shouldUpdate)
    {
        items.clear();

        if (shouldUpdate)
        {
            update();
        }
    }

    @Override
    public void addItems(List<StartTag> items)
    {
        addItems(items, true);
    }

    private void addItems(List<StartTag> items, boolean shouldUpdate)
    {
        this.items.addAll(items);

        if (shouldUpdate)
        {
            update();
        }
    }

    @Override
    public void setSuggestWords(List<String> words)
    {
        oracle.clear();
        oracle.addAll(words);
    }

    @UiField(provided=true) final TzSupport tzSupport;

    private void update()
    {
        Collections.reverse(items);

        table.removeAllRows();

        int row = 0;

        int rowOfNow = 0;
        String now = tzSupport.renderForClientMachine(new Date());

        for (final StartTag item: items)
        {
            int col = 0;

            // need the max, but not beyond 'now'.
            if (now.compareTo(item.getInstantString()) >= 0)
            {
                rowOfNow = row;
            }

            Anchor resumeLink = new Anchor(constants.resumeTextIcon());
            resumeLink.setTitle(constants.resumeHint());
            resumeLink.setStylePrimaryName("tsTaskResumeLink");

            resumeLink.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    fireInterestingThing(item.getDescription());
                }
            });

            if (!item.getPast())
            {
                table.getRowFormatter().addStyleName(row, "ts-task-future");
            }

            SuggestBox suggestBox = new SuggestBox(oracle);
            suggestBox.setAutoSelectEnabled(false);
            EditableLabel itemLabel = new EditableLabel(suggestBox, item.getDescription());
            itemLabel.getEditor().setWidth("30em");
            itemLabel.addListener(new EditableLabel.Listener()
            {
                @Override
                public void editCanceled()
                {
                }

                @Override
                public void editBegun()
                {
                }

                @Override
                public void editAccepted(String oldValue, String newValue)
                {
                    fireEdited(
                            new StartTag(
                                item.getInstantString(),
                                item.getUntilString(),
                                item.getDurationMillis(),
                                newValue,
                                null
                                ));
                }
            });

            final EditableLabel timeLabel = new EditableLabel(formatDuration(item.getDurationMillis().longValue()));
            timeLabel.getEditor().setWidth("20em");
            timeLabel.addListener(new EditableLabel.Listener()
            {
                @Override
                public void editBegun()
                {
                    timeLabel.getEditor().setValue(item.getInstantString());
                }

                @Override
                public void editCanceled()
                {
                }

                @Override
                public void editAccepted(String oldValue, String newValue)
                {
                    fireTimeEdited(
                            new StartTag(
                                    newValue,
                                    null,
                                    null,
                                    item.getDescription(),
                                    null
                                    ));
                }
            });

            HorizontalPanel hp = new HorizontalPanel();
            hp.setStylePrimaryName("tsTaskRow");
            hp.add(resumeLink);
            hp.add(itemLabel);

            table.setWidget(row, col, hp);
            table.getCellFormatter().setAlignment(row, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
            ++col;

            table.setWidget(row, col, timeLabel);
            table.getCellFormatter().setAlignment(row, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            ++col;

            Label spacer = new Label();
            spacer.setWidth("2em");
            table.setWidget(row, col, spacer);
            table.getCellFormatter().setAlignment(row, col, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
            ++col;

            ++row;
        }

        table.getRowFormatter().addStyleName(rowOfNow, "ts-task-current");

        scroller.scrollToBottom();
        scroller.scrollToRight();
    }
}
