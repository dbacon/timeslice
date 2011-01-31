package com.enokinomi.timeslice.web.prorata.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SplitProjectDialogBox extends DialogBox
{
    private final SplitProjectDialogBoxConstants constants = GWT.create(SplitProjectDialogBoxConstants.class);
    private final SplitProjectDialogBoxMessages messages = GWT.create(SplitProjectDialogBoxMessages.class);

    private final Label w2 = new Label(constants.targetProject());
    private final TextBox w = new TextBox();
    private final TextBox weightTextBox = new TextBox();
    private final Button okButton = new Button(constants.ok());
    private final Button cancelButton = new Button(constants.cancel());
    private final String projectName;
    private final SplitProjectDialogBox.Listener listener;

    public static interface Listener
    {
        void added(String project, String splitTo, Double weight);
    }

    SplitProjectDialogBox(String projectName, SplitProjectDialogBox.Listener listener)
    {
        super(false, true);

        this.projectName = projectName;
        this.listener = listener;

        setText(messages.splitProjectTitle(projectName));

        weightTextBox.setWidth("3em");
        weightTextBox.setValue("1");

        HorizontalPanel hp1 = new HorizontalPanel();
        hp1.add(okButton);
        hp1.add(cancelButton);

        FlexTable tb = new FlexTable();

        tb.setWidget(0, 0, w2);
        tb.setWidget(0, 1, w);
        tb.setWidget(1, 0, new Label(constants.weight()));
        tb.setWidget(1, 1, weightTextBox);

        VerticalPanel vp = new VerticalPanel();
        vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        vp.add(tb);
        vp.add(hp1);

        add(vp);

        okButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                SplitProjectDialogBox.this.hide();

                if (isSplitProjectSelected())
                {
                    if (null != SplitProjectDialogBox.this.listener)
                    {
                        SplitProjectDialogBox.this.listener.added(
                                SplitProjectDialogBox.this.projectName,
                                getSplitProjectName(),
                                getWeight());
                    }
                }
            }
        });

        cancelButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                w.setText("");
                SplitProjectDialogBox.this.hide();
            }
        });

        w.addKeyDownHandler(new KeyDownHandler()
        {
            @Override
            public void onKeyDown(KeyDownEvent event)
            {
                if (KeyCodes.KEY_ENTER == event.getNativeKeyCode())
                {
                    okButton.click();
                }
            }
        });

        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                w.setFocus(true);
                w.selectAll();
            }
        });
    }

    public boolean isSplitProjectSelected()
    {
        return !w.getText().trim().isEmpty();
    }

    public String getSplitProjectName()
    {
        if (isSplitProjectSelected()) return w.getText();
        return null;
    }

    public Double getWeight()
    {
        return Double.valueOf(weightTextBox.getValue());
    }
}
