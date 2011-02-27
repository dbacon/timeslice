package com.enokinomi.timeslice.web.report.client.presenter;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ReportPlace extends Place
{
    private final Date when;
    private final String path;


    public Date getWhen()
    {
        return when;
    }

    public String getPath()
    {
        return path;
    }

    public static class Tokenizer implements PlaceTokenizer<ReportPlace>
    {
        @Override
        public ReportPlace getPlace(String token)
        {
            String path = null;
            Date when = null;

            if (token != null)
            {
                String[] pieces = token.split(";");

                for (String piece: pieces)
                {
                    String[] nv = piece.split("=");
                    if (nv.length == 2)
                    {
                        String name = nv[0];
                        String value = nv[1];

                        if ("d".equals(name))
                        {
                            try
                            {
                                when = DateTimeFormat.getFormat("yyyy-MM-dd").parse(value);
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        else if ("p".equals(name))
                        {
                            path = value;
                        }
                    }
                }
            }

            return new ReportPlace(when, path);
        }

        @Override
        public String getToken(ReportPlace place)
        {
            String t = "";
            if (place.when != null)
            {
                if (t.length() > 0) t += ";";
                t += "d=" + DateTimeFormat.getFormat("yyyy-MM-dd").format(place.when);
            }
            if (place.path != null)
            {
                if (t.length() > 0) t += ";";
                t += "p=" + place.path;
            }

            return t;
        }
    }

    public ReportPlace(Date when, String path)
    {
        this.when = when;
        this.path = path;
    }

    @Override
    public String toString()
    {
        return "Reports";
    }

}
