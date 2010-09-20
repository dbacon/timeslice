package com.enokinomi.timeslice.lib.assign;

import org.joda.time.DateTime;

public interface INowProvider
{
    DateTime getNow();
}
