package com.enokinomi.timeslice.web.core.client.util;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;

public interface IActivityFactory
{
    Activity get(Place place);
}
