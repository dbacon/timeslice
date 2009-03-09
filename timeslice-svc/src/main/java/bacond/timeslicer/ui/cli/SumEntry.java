package bacond.timeslicer.ui.cli;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import bacond.lib.util.Narrow;
import bacond.timeslicer.app.dto.StartTag;
import bacond.timeslicer.app.processing.Aggregate;
import bacond.timeslicer.app.processing.Split;
import bacond.timeslicer.app.processing.TaskTotal;

public class SumEntry
{
	public static void main(String[] args) throws IOException
	{
		List<StartTag> items = readItems(new FileInputStream(args[0]));
		
		List<StartTag> items2 = new Split().split(items, new Instant());
		Map<String, TaskTotal> sums = new Aggregate().sumThem(new Aggregate().aggregate(items2));
	
		System.out.println("totals:");
		for (TaskTotal total: sums.values())
		{
			System.out.printf("TOTAL#%s#%4.2f#%s\n", total.getWho(), (total.getMillis() / 1000.) / 3600., total.getWhat());
		}
	}

	private static List<StartTag> readItems(InputStream in) throws IOException
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

	private static StartTag fromLine(String line)
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
