package com.enokinomi.timeslice.web.task.client.ui_compat.api;

import java.util.List;

import com.enokinomi.timeslice.web.task.client.core.StartTag;

public interface ITs107Reader
{

    List<StartTag> parseItems(String text);

}
