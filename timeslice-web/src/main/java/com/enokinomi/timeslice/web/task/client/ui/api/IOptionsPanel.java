package com.enokinomi.timeslice.web.task.client.ui.api;


public interface IOptionsPanel extends IIsWidget, IOptionsProvider
{
    void addOptionsListener(IOptionsListener iOptionsListener);
}
