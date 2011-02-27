package com.enokinomi.timeslice.web.top.client.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import com.enokinomi.timeslice.web.core.client.util.IActivityFactory;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

public class GenericActivityMapper implements ActivityMapper
{
    private final Map<Class<? extends Place>, IActivityFactory> map = new LinkedHashMap<Class<? extends Place>, IActivityFactory>();

    public void map(Class<? extends Place> placeType, IActivityFactory activityFactory)
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
        IActivityFactory activityStarter = map.get(place.getClass());
        if (activityStarter != null) return activityStarter.get(place);
        return null;
    }
}
