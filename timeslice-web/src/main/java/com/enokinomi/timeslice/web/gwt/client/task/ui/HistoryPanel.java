package com.enokinomi.timeslice.web.gwt.client.task.ui;

import static com.enokinomi.timeslice.web.gwt.client.task.ui.HumanReadableTimeHelper.formatDuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.controller.EditableLabel;
import com.enokinomi.timeslice.web.gwt.client.controller.EditableLabel.Listener;
import com.enokinomi.timeslice.web.gwt.client.task.core.StartTag;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public class HistoryPanel extends ResizeComposite
{
    TaskPanelConstants constants = GWT.create(TaskPanelConstants.class);

    private final FlexTable table = new FlexTable();
    private final ScrollPanel scroller = new ScrollPanel(table);

    private final List<StartTag> items = new ArrayList<StartTag>();

    private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

    public static interface IHistoryPanelListener
    {
        void interestingThing(String p);
        void fireEdited(StartTag startTag);
        void fireTimeEdited(StartTag startTag);
        void hotlisted(String name, String description);
        void editModeEntered();
        void editModeLeft();
    }

    private final List<IHistoryPanelListener> listeners = new ArrayList<IHistoryPanelListener>();

    public void addHistoryPanelListener(IHistoryPanelListener listener)
    {
        listeners.add(listener);
    }

    public void removeHistoryPanelListener(IHistoryPanelListener listener)
    {
        listeners.remove(listener);
    }

    protected void fireEditModeEntered()
    {
        for (IHistoryPanelListener listener: listeners)
        {
            listener.editModeEntered();
        }
    }

    protected void fireEditModeLeft()
    {
        for (IHistoryPanelListener listener: listeners)
        {
            listener.editModeLeft();
        }
    }

    protected void fireHotlisted(String name, String description)
    {
        for (IHistoryPanelListener listener: listeners)
        {
            listener.hotlisted(name, description);
        }
    }

    protected void fireInterestingThing(String p)
    {
        for (IHistoryPanelListener listener: listeners)
        {
            listener.interestingThing(p);
        }
    }

    protected void fireEdited(StartTag startTag)
    {
        for (IHistoryPanelListener listener: listeners)
        {
            listener.fireEdited(startTag);
        }
    }

    protected void fireTimeEdited(StartTag startTag)
    {
        for (IHistoryPanelListener listener: listeners)
        {
            listener.fireTimeEdited(startTag);
        }
    }

    public HistoryPanel()
    {
        table.setWidth("100%");
        table.getColumnFormatter().setWidth(2, "10em");
        table.getColumnFormatter().addStyleName(1, "tsTimeField");
        initWidget(scroller);
    }

    public void clear(boolean shouldUpdate)
    {
        items.clear();

        if (shouldUpdate)
        {
            update();
        }
    }

    public void clear()
    {
        clear(true);
    }

    public void addItems(List<StartTag> items)
    {
        addItems(items, true);
    }

    public void addItems(List<StartTag> items, boolean shouldUpdate)
    {
        this.items.addAll(items);

        if (shouldUpdate)
        {
            update();
        }
    }

    public void setSuggestWords(List<String> words)
    {
        oracle.clear();
        oracle.addAll(words);
    }

    protected void update()
    {
        Collections.reverse(items);

        table.removeAllRows();

        int row = 0;

        for (final StartTag item: items)
        {
            int col = 0;

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

            EditableLabel itemLabel = new EditableLabel(new SuggestBox(oracle), item.getDescription());
            itemLabel.getEditor().setWidth("30em");
            itemLabel.addListener(new Listener()
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
            timeLabel.addListener(new Listener()
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

        scroller.scrollToBottom();
        scroller.scrollToRight();
    }
}
