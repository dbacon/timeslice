package bacond.timeslicer.ui.cli;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import bacond.timeslicer.app.processing.Aggregate;
import bacond.timeslicer.app.processing.Split;
import bacond.timeslicer.app.task.StartTag;
import bacond.timeslicer.app.task.StartTagIo;
import bacond.timeslicer.app.tasktotal.TaskTotal;

public class SumEntry
{
	public static void main(String[] args) throws IOException
	{
		List<StartTag> items = new StartTagIo().readItems(new FileInputStream(args[0]));

		List<StartTag> items2 = new Split().split(items, new Instant());
		Map<String, TaskTotal> sums = new Aggregate().sumThem(new Aggregate().aggregate(items2));

		System.out.println("totals:");
		for (TaskTotal total: sums.values())
		{
			System.out.printf("TOTAL#%s#%4.2f#%s\n", total.getWho(), (total.getMillis() / 1000.) / 3600., total.getWhat());
		}
	}

}
