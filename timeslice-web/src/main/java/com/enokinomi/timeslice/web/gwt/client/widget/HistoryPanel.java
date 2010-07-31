package com.enokinomi.timeslice.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.enokinomi.timeslice.web.gwt.client.beans.StartTag;
import com.enokinomi.timeslice.web.gwt.client.widget.TaskPanel.ITaskPanelListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;

public class HistoryPanel extends ResizeComposite
{
    private final FlowPanel itemsPanel = new FlowPanel();
    private final ScrollPanel scroller = new ScrollPanel(itemsPanel);

    private final List<StartTag> items = new ArrayList<StartTag>();

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

    public class TaskPanelListener implements ITaskPanelListener
    {
        public void resumeClicked(StartTag historicStartTag)
        {
            fireInterestingThing(historicStartTag.getDescription());
        }

        public void itemEdited(StartTag editedTag)
        {
            fireEdited(editedTag);
        }

        public void timeEdited(StartTag newTag)
        {
            fireTimeEdited(newTag);
        }

        public void itemHotlisted(String name, String description)
        {
            fireHotlisted(name, description);
        }

        @Override
        public void editModeEntered(StartTag tag)
        {
            fireEditModeEntered();
        }

        @Override
        public void editModeLeft(StartTag tag)
        {
            fireEditModeLeft();
        }

    }

    final TaskPanelListener listener = new TaskPanelListener();

    protected void update()
    {
        Collections.reverse(items);

        itemsPanel.clear();
        for (StartTag item: items)
        {
            TaskPanel taskPanel = new TaskPanel(item);
            taskPanel.addTaskPanelListener(listener);
            itemsPanel.add(taskPanel);
        }

        scroller.scrollToBottom();
        scroller.scrollToRight();
    }
}
