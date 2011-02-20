package com.enokinomi.timeslice.web.core.client.ui;

import java.util.List;

public class GenericRegistration<L> implements Registration
{
    private final List<L> listeners;
    private final L listener;

    public static <T> GenericRegistration<T> wrap(List<T> listeners, T listener)
    {
        return new GenericRegistration<T>(listeners, listener);
    }

    public GenericRegistration(List<L> listeners, L listener)
    {
        this.listeners = listeners;
        this.listener = listener;
    }

    @Override
    public void terminate()
    {
        listeners.remove(listener);
    }
}
