package com.enokinomi.timeslice.web.task.client.controller.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginDialog extends DialogBox
{
    private final LoginDialogConstants constants = GWT.create(LoginDialogConstants.class);

    private final TextBox userText = new TextBox();
    private final PasswordTextBox passwordText = new PasswordTextBox();
    private Button loginButton = new Button(constants.login());
    private Button cancelButton = new Button(constants.cancel());


    public static interface IListener
    {
        void submitted(String user, String password);
        void canceled();
    }

    private final LoginDialog.IListener listener;

    private void doCancel()
    {
        LoginDialog.this.hide();
        if (null != listener)
        {
            listener.canceled();
        }
    }

    private void doSubmit()
    {
        LoginDialog.this.hide();
        if (null != listener)
        {
            listener.submitted(userText.getText(), passwordText.getText());
        }
    }

    private void scheduleSubmit()
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                if (!userText.getText().isEmpty() && !passwordText.getText().isEmpty())
                {
                    doSubmit();
                }
            }
        });
    }

    private void scheduleButtonSensitivity()
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                loginButton.setEnabled(!userText.getText().isEmpty() && !passwordText.getText().isEmpty());
            }
        });
    }

    LoginDialog(String title, String subText, LoginDialog.IListener plistener)
    {
        super(false, true);

        this.listener = plistener;

        Label titleLabel = new Label(title);
        Label subtextLabel = null;
        if (null != subText) subtextLabel = new Label(subText);

        FlexTable table = new FlexTable();
        table.setWidget(0, 0, new Label(constants.user()));
        table.setWidget(0, 1, userText);
        table.setWidget(1, 0, new Label(constants.password()));
        table.setWidget(1, 1, passwordText);
        table.setWidget(2, 0, cancelButton);
        table.setWidget(2, 1, loginButton);

        VerticalPanel vp = new VerticalPanel();
        vp.add(titleLabel);
        if (null != subtextLabel) vp.add(subtextLabel);
        vp.add(table);

        userText.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                scheduleButtonSensitivity();

                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    passwordText.setFocus(true);
                }
            }
        });

        passwordText.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                scheduleButtonSensitivity();

                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                {
                    scheduleSubmit();
                }
            }
        });

        scheduleButtonSensitivity();


        loginButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                doSubmit();
            }
        });

        cancelButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                doCancel();
            }
        });

        setText(constants.login());
        setGlassEnabled(true);
        setAnimationEnabled(true);
        setWidget(vp);
    }

    @Override
    public void show()
    {
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                userText.setFocus(true);
            }
        });

        super.show();
    }
}
