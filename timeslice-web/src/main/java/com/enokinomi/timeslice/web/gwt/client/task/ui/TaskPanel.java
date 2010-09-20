package com.enokinomi.timeslice.web.gwt.client.task.ui;

import static com.enokinomi.timeslice.web.gwt.client.task.ui.HumanReadableTimeHelper.formatDuration;

import java.util.ArrayList;
import java.util.List;


import com.enokinomi.timeslice.web.gwt.client.task.core.StartTag;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;

public class TaskPanel extends ResizeComposite
{
    public static interface ITaskPanelListener
    {
        void resumeClicked(StartTag historicStartTag);
        void itemEdited(StartTag editedTag);
        void timeEdited(StartTag newTag);
        void editModeEntered(StartTag tag);
        void editModeLeft(StartTag tag);
    }

    private final List<ITaskPanelListener> listeners = new ArrayList<ITaskPanelListener>();
    private final HorizontalPanel descriptionContainer= new HorizontalPanel();
    private final Label label = new Label();
    private final TextBox descriptionEditor = new TextBox();
    private final HorizontalPanel timeContainer = new HorizontalPanel();
    private final Label timeLabel = new Label();
    private final TextBox timeEditor = new TextBox();
    private boolean losingFocusAccepts = false;

    public void addTaskPanelListener(ITaskPanelListener listener)
    {
        listeners.add(listener);
    }

    public void removeTaskPanelListener(ITaskPanelListener listener)
    {
        listeners.remove(listener);
    }

    protected void fireEditModeEntered(StartTag startTag)
    {
        for (ITaskPanelListener listener: listeners)
        {
            listener.editModeEntered(startTag);
        }
    }

    protected void fireEditModeLeft(StartTag startTag)
    {
        for (ITaskPanelListener listener: listeners)
        {
            listener.editModeLeft(startTag);
        }
    }

    protected void fireResumeClicked(StartTag startTag)
    {
        for (ITaskPanelListener listener: listeners)
        {
            listener.resumeClicked(startTag);
        }
    }

    protected void fireEdited(StartTag startTag)
    {
        for (ITaskPanelListener listener: listeners)
        {
            listener.itemEdited(startTag);
        }
    }

    protected void fireTimeEdited(StartTag startTag)
    {
        for (ITaskPanelListener listener: listeners)
        {
            listener.timeEdited(startTag);
        }
    }

    private void editModeOn(final StartTag startTag)
    {
        fireEditModeEntered(startTag);
        label.setVisible(false);
        descriptionEditor.setText(startTag.getDescription());
        descriptionEditor.setVisible(true);
        descriptionEditor.selectAll();
        descriptionEditor.setFocus(true);
    }

    private void editModeOn2(final StartTag startTag)
    {
        fireEditModeEntered(startTag);
        timeLabel.setVisible(false);
        timeEditor.setText(startTag.getInstantString());
        timeEditor.setVisible(true);
        timeEditor.selectAll();
        timeEditor.setFocus(true);
    }

    private void editModeOff(final StartTag startTag, boolean accepted)
    {
        descriptionEditor.setVisible(false);
        label.setText(startTag.getDescription());
        label.setVisible(true);

        if (accepted)
        {
            startTag.setDescription(descriptionEditor.getText());
            label.setText(descriptionEditor.getText());
            fireEdited(new StartTag(
                    startTag.getInstantString(),
                    startTag.getUntilString(),
                    startTag.getDurationMillis(),
                    descriptionEditor.getText().trim(),
                    startTag.getPast()));
        }

        fireEditModeLeft(startTag);
    }

    private void editModeOff2(final StartTag startTag, boolean accepted)
    {
        timeEditor.setVisible(false);
        timeLabel.setText(formatDuration(startTag.getDurationMillis().longValue()));
        timeLabel.setVisible(true);

        if (accepted)
        {
            startTag.setInstantString(timeEditor.getText());
            timeLabel.setText(formatDuration(startTag.getDurationMillis().longValue()));
            fireTimeEdited(new StartTag(
                timeEditor.getText(),
                startTag.getUntilString(),
                startTag.getDurationMillis(),
                startTag.getDescription(),
                false));
        }

        fireEditModeLeft(startTag);
    }

    public TaskPanel(final StartTag startTag)
    {
        Anchor resumeLink = new Anchor("[R]");
        resumeLink.setTitle("Resume this task");
        resumeLink.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                fireResumeClicked(startTag);
            }
        });

        descriptionEditor.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                if (KeyCodes.KEY_ENTER == event.getCharCode())
                {
                    editModeOff(startTag, true);
                }
                else if (KeyCodes.KEY_ESCAPE == event.getCharCode())
                {
                    editModeOff(startTag, false);
                }
            }
        });


        label.setText(startTag.getDescription());
        label.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                editModeOn(startTag);
            }
        });

        descriptionContainer.add(label);
        descriptionContainer.add(descriptionEditor);
        descriptionEditor.setVisible(false);

        descriptionEditor.addBlurHandler(new BlurHandler()
        {
            @Override
            public void onBlur(BlurEvent event)
            {
                if (descriptionEditor.isVisible())
                {
                    editModeOff(startTag, losingFocusAccepts);
                }
            }
        });

        timeLabel.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                editModeOn2(startTag);
            }
        });
        timeContainer.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        timeContainer.add(timeLabel);
        timeContainer.add(timeEditor);
        timeEditor.setVisible(false);
        timeEditor.addBlurHandler(new BlurHandler()
        {
            @Override
            public void onBlur(BlurEvent event)
            {
                if (timeEditor.isVisible())
                {
                    editModeOff2(startTag, losingFocusAccepts);
                }
            }
        });
        timeEditor.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                if (KeyCodes.KEY_ENTER == event.getCharCode())
                {
                    editModeOff2(startTag, true);
                }
                else if (KeyCodes.KEY_ESCAPE == event.getCharCode())
                {
                    editModeOff2(startTag, false);
                }
            }
        });
        if (null != startTag.getUntilString())
        {
            timeLabel.setText(formatDuration(startTag.getDurationMillis().longValue()));
        }

        DockLayoutPanel dp = new DockLayoutPanel(Unit.EM);
        dp.setTitle(startTag.getInstantString());
        dp.setSize("100%", "1.5em");
        dp.addWest(resumeLink, 1.5);
        dp.addEast(timeContainer, 20);
        timeContainer.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        dp.add(descriptionContainer);

        initWidget(dp);

        setStylePrimaryName("ts-task");
        if (!startTag.getPast())
        {
            addStyleDependentName("future");
        }
        else
        {
            removeStyleDependentName("future");
        }
    }
}
