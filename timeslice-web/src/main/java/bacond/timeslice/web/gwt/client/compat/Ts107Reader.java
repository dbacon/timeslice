package bacond.timeslice.web.gwt.client.compat;

import java.util.LinkedList;
import java.util.List;

import bacond.timeslice.web.gwt.client.beans.StartTag;

public class Ts107Reader
{
    private final String text;

    public Ts107Reader(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

    public List<StartTag> parseItems()
    {
        LinkedList<StartTag> result = new LinkedList<StartTag>();
        String[] lines = getText().split("\n");

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
