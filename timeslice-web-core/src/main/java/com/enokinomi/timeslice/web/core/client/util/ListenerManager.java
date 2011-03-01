package com.enokinomi.timeslice.web.core.client.util;

import java.util.ArrayList;
import java.util.List;



public class ListenerManager<L>
{
    private final List<L> listeners = new ArrayList<L>();

    public List<L> getListeners() { return new ArrayList<L>(listeners); }

    public Registration addListener(L l)
    {
        if (l != null)
        {
            listeners.add(l);
            return Registration.wrap(listeners, l);
        }
        return Registration.Null;
    }

}
