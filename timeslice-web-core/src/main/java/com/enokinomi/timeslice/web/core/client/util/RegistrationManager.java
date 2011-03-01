package com.enokinomi.timeslice.web.core.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



public class RegistrationManager
{
    private List<Registration> registrations = new ArrayList<Registration>();

    public RegistrationManager add(Registration r)
    {
        registrations.add(r);
        return this;
    }

    public RegistrationManager addAll(Collection<? extends Registration> items)
    {
        registrations.addAll(items);
        return this;
    }

    public RegistrationManager terminateAll()
    {
        for (Registration r: registrations) r.terminate();
        return this;
    }
}
