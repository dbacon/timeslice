package com.enokinomi.timeslice.app.rolodex;

import java.util.List;

public interface IRolodex
{
    List<ClientInfo> getClientInfos();

    boolean isWritable();
    void addClientInfo(ClientInfo name);
}