package com.enokinomi.timeslice.web.gwt.client.controller;

import com.google.gwt.i18n.client.Constants;

public interface LoginDialogConstants extends Constants
{
    @DefaultStringValue("Login")
    String login();

    @DefaultStringValue("Cancel")
    String cancel();

    @DefaultStringValue("User")
    String user();

    @DefaultStringValue("Password")
    String password();
}