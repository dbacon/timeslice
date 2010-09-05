package com.enokinomi.timeslice.app.assign;

import org.joda.time.DateTime;

public interface INowProvider
{
    DateTime getNow();
}
