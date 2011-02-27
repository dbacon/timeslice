package com.enokinomi.timeslice.web.core.client.util;

import java.util.List;


public abstract class Registration
{
    public static final Registration Null = new Registration() { @Override public void terminate() { } };

    public static <L> Registration wrap(List<L> listeners, L listener) { return new GenericRegistration<L>(listeners, listener); }

    public abstract void terminate();

    public static class GenericRegistration<L> extends Registration
    {
        private final List<L> listeners;
        private final L listener;

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
}
