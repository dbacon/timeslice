package com.enokinomi.timeslice.web.core.client.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class ListenerManager<L>
{
    private final List<L> listeners = new ArrayList<L>();

    public List<L> getListeners() { return Collections.unmodifiableList(listeners); }

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
