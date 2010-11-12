package com.enokinomi.timeslice.web.task.client.ui_compat.impl;

import java.util.LinkedList;
import java.util.List;

import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.ui_compat.api.ITs107Reader;


public class Ts107Reader implements ITs107Reader
{
    Ts107Reader()
    {
    }

    @Override
    public List<StartTag> parseItems(String text)
    {
        LinkedList<StartTag> result = new LinkedList<StartTag>();
        String[] lines = text.split("\n");

        for (String line: lines)
        {
            line = line.trim();
            if (line.startsWith("[") && line.endsWith("]") && line.split("#").length == 3)
            {
                String[] fields = line.substring(1, line.length() - 1).split("#");
                //String who = fields[0];
                String instant = fields[1];
                String task = fields[2];

                // some validation to raise certainty that this field is a joda time string.
                // (we don't have regex lib in gwt jre lib)
                String[] timedate = instant.split("T");
                if (timedate.length != 2) continue;
                String date = timedate[0];
                String[] datepieces = date.split("-");
                if (datepieces.length != 3) continue;
                if (datepieces[0].length() != 4) continue;
                if (datepieces[1].length() != 2) continue;
                if (datepieces[2].length() != 2) continue;
                String time = timedate[1];
                String[] timepieces = time.split(":");
                if (timepieces.length < 3) continue;
                if (timepieces[0].length() != 2) continue;
                if (timepieces[1].length() != 2) continue;

                result.add(new StartTag(instant, null, null, task, true));
            }
        }

        return result;
    }
}
