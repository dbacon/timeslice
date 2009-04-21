package bacond.timeslicer.app.processing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bacond.lib.util.Transforms;
import bacond.timeslicer.app.task.api.StartTag;
import bacond.timeslicer.app.tasktotal.api.TaskTotal;

public class Aggregate
{
	public Map<String, List<StartTag>> aggregate(List<StartTag> items)
	{
		return Bucket.create(Transforms.member(StartTag.class, String.class, "what")).bucket(items).getBuckets();
	}

	public Map<String, TaskTotal> sumThem(Map<String, List<StartTag>> buckets)
	{
		Map<String, TaskTotal> result = new LinkedHashMap<String, TaskTotal>();

		for (Entry<String, List<StartTag>> entry: buckets.entrySet())
		{
			result.put(entry.getKey(), new Sum().sum(entry.getValue()));
		}

		return result;
	}
}
