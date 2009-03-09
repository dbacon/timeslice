package bacond.timeslicer.app.processing;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import bacond.timeslicer.app.dto.StartTag;


public class AggregateTest
{
	@Test
	public void test_0()
	{
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

		List<StartTag> items = new LinkedList<StartTag>();

		Instant start = fmt.parseDateTime("2008/03/09 12:34:56").toInstant();
		Instant click = start.plus(3453496);
		items.add(new StartTag("bacond", start, "hello", click));
		
		start = click;
		click = click.plus(98634);
		items.add(new StartTag("bacond", start, "hello bye", click));
		
		start = click;
		click = click.plus(8742);
		items.add(new StartTag("bacond", start, "hello", click));
		
		Map<String, List<StartTag>> buckets = new Aggregate().aggregate(items);
		
		System.out.println("buckets:");
		
		for (Entry<String, List<StartTag>> entry: buckets.entrySet())
		{
			System.out.println("key: " + entry.getKey());
			System.out.println("    value: " + entry.getValue());
		}
	}

	@Test
	public void test_1()
	{
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

		List<StartTag> items = new LinkedList<StartTag>();

		Instant start = fmt.parseDateTime("2008/03/09 12:34:56").toInstant();
		Instant click = start.plus(1400);
		items.add(new StartTag("bacond", start, "hello", click));
		
		start = click;
		click = click.plus(1300);
		items.add(new StartTag("bacond", start, "hello bye", click));
		
		start = click;
		click = click.plus(1200);
		items.add(new StartTag("bacond", start, "hello", click));
		
		Map<String, List<StartTag>> buckets = new Aggregate().aggregate(items);
		
		Map<String, TaskTotal> sums = new Aggregate().sumThem(buckets);
		
		System.out.println("sums:");
		
		for (Entry<String, TaskTotal> entry: sums.entrySet())
		{
			System.out.println("key: " + entry.getKey());
			System.out.println("    value: " + entry.getValue());
		}
		
		assertEquals(2, sums.size());
		assertEquals(1300, sums.get("hello bye").getMillis());
		assertEquals(2600, sums.get("hello").getMillis());
	}
}