package com.enokinomi.timeslice.web.appjob.client.ui.impl;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class AppJobPlace extends Place
{
    public static class Tokenizer implements PlaceTokenizer<AppJobPlace>
    {
        @Override
        public AppJobPlace getPlace(String token)
        {
            return new AppJobPlace();
        }

        @Override
        public String getToken(AppJobPlace place)
        {
            return "";
        }
    }

    public AppJobPlace()
    {
    }

    @Override
    public String toString()
    {
        return "Maintenance";
    }

}
