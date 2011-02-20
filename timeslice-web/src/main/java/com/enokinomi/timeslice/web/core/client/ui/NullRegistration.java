package com.enokinomi.timeslice.web.core.client.ui;

public class NullRegistration implements Registration
{
    public static NullRegistration Instance = new NullRegistration();
    @Override public void terminate() { }
}
