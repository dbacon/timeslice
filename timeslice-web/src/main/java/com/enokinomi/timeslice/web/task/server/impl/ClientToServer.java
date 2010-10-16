package com.enokinomi.timeslice.web.task.server.impl;

import com.enokinomi.timeslice.lib.util.ITransform;
import com.enokinomi.timeslice.web.task.client.core.StartTag;

public class ClientToServer implements ITransform<StartTag, com.enokinomi.timeslice.lib.task.StartTag>
{
    private final String user;

    ClientToServer(String user)
    {
        this.user = user;
    }

    @Override
    public com.enokinomi.timeslice.lib.task.StartTag apply(StartTag r)
    {
        return new com.enokinomi.timeslice.lib.task.StartTag(user, r.getInstantString(), r.getDescription(), null);
    }
}
