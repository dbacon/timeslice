package com.enokinomi.timeslice.web.task.client.ui_one.api;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

public class GenericActivityMapper implements ActivityMapper
{
    public static interface ActivityFactory
    {
        Activity get(Place place);
    }

    private final Map<Class<? extends Place>, ActivityFactory> map = new LinkedHashMap<Class<? extends Place>, ActivityFactory>();

    public void map(Class<? extends Place> placeType, ActivityFactory activityFactory)
    {
        map.put(placeType, activityFactory);
    }

    @Inject
    GenericActivityMapper()
    {
    }

    @Override
    public Activity getActivity(Place place)
    {
        ActivityFactory activityStarter = map.get(place.getClass());
        if (activityStarter != null) return activityStarter.get(place);
        return null;
    }
}
