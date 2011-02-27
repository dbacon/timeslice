package com.enokinomi.timeslice.web.settings.client.presenter.impl;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class OptionsPlace extends Place
{
    public static class Tokenizer implements PlaceTokenizer<OptionsPlace>
    {
        @Override
        public OptionsPlace getPlace(String token)
        {
            if (token != null)
            {
            }

            return new OptionsPlace();
        }

        @Override
        public String getToken(OptionsPlace place)
        {
            return "";
        }
    }

    public OptionsPlace()
    {
    }

    @Override
    public String toString()
    {
        return "Options";
    }

}
