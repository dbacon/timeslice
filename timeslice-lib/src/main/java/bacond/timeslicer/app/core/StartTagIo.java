package bacond.timeslicer.app.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import bacond.lib.util.Narrow;

public class StartTagIo
{
    public List<StartTag> readItems(InputStream in) throws IOException
    {
        List<String> lines = Narrow.<String>fromList(IOUtils.readLines(in, "UTF8"));

        List<StartTag> result = new ArrayList<StartTag>(lines.size());
        for (String line: lines)
        {
            StartTag tag = fromLine(line);
            if (null != tag)
            {
                result.add(tag);
            }
        }

        return result;
    }

    public String toLine(StartTag tag)
    {
        return String.format("[%s#%s#%s]",
                tag.getWho(),
                ISODateTimeFormat.dateTime().print(tag.getWhen()),
                tag.getWhat());
    }

    public StartTag fromLine(String line)
    {
        StartTag result = null;

        if (line.startsWith("[") && line.endsWith("]"))
        {
            line = line.substring(1, line.length() - 1);

            String[] fields = line.split("#");

            if (fields.length == 3)
            {
                String who = fields[0];
                Instant when = ISODateTimeFormat.dateTime().parseDateTime(fields[1]).toInstant();
                String what = fields[2];
                Instant until = null; //ISODateTimeFormat.dateTime().parseDateTime(fields[3]).toInstant();

                result = new StartTag(who, when, what, until);
            }
        }

        return result;
    }
}
