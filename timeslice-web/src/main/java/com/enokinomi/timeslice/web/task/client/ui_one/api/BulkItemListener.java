package com.enokinomi.timeslice.web.task.client.ui_one.api;

import java.util.List;

import com.enokinomi.timeslice.web.task.client.core.StartTag;

public interface BulkItemListener
{
    void addItems(List<StartTag> items);
}
