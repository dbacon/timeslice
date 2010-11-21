package com.enokinomi.timeslice.lib.assign.api;

import org.joda.time.DateTime;

public interface INowProvider
{
    DateTime getNow();
}
