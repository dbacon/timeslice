package com.enokinomi.timeslice.web.task.client.presenter;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class InputPlace extends Place
{
    private final boolean current;
    private final Date when;
    private final String creator;

    public boolean isCurrent()
    {
        return current;
    }

    public Date getWhen()
    {
        return when;
    }

    public String getCreator()
    {
        return creator;
    }

    public static class Tokenizer implements PlaceTokenizer<InputPlace>
    {
        @Override
        public InputPlace getPlace(String token)
        {
            GWT.log("input.get-place: '" + token + "'");
            String[] pieces = token.split("/");

            if (pieces.length > 0)
            {
                String one = pieces[0];

                if ("current".equals(one))
                {
                    // same as default.
                }
                else if ("history".equals(one))
                {
                    if (pieces.length > 1)
                    {
                        String date = pieces[1];
                        Date when = DateTimeFormat.getFormat("yyyy-MM-dd").parse(date);
                        return new InputPlace("from-token:" + token, false, when);
                    }
                    else
                    {
                        return new InputPlace("from-token:" + token, false, null);
                    }
                }
                else
                {
                    // leave as default
                }
            }

            return new InputPlace("from-token:" + token, true, null);
        }

        @Override
        public String getToken(InputPlace place)
        {
            if (place.current) return "current";
            String token = "history" + "/" + DateTimeFormat.getFormat("yyyy-MM-dd").format(place.when);
            GWT.log("Got token for input-place: " + token);
            return token;
        }
    }

    public InputPlace(String creator, boolean current, Date when)
    {
        this.creator = creator;
        this.current = current;
        this.when = when;
    }

    @Override
    public String toString()
    {
        return "Input";
    }

}
