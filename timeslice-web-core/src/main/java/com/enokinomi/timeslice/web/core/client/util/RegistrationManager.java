package com.enokinomi.timeslice.web.core.client.util;

import java.util.ArrayList;
import java.util.List;



public class RegistrationManager
{
    private List<Registration> registrations = new ArrayList<Registration>();

    public void add(Registration r)
    {
        registrations.add(r);
    }

    public void terminateAll()
    {
        for (Registration r: registrations) r.terminate();
    }
}
