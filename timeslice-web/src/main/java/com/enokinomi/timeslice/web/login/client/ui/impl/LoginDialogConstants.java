package com.enokinomi.timeslice.web.login.client.ui.impl;

import com.google.gwt.i18n.client.Constants;

interface LoginDialogConstants extends Constants
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
