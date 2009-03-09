package bacond.timeslicer.app.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import bacond.timeslicer.app.dto.StartTag;
import bacond.timeslicer.app.restlet.resource.CompareByTime;

public class Split
{
	public List<StartTag> split(List<StartTag> tags, Instant endInstantOfLastTasks)
	{
		List<StartTag> localTags = new ArrayList<StartTag>(tags);
		Collections.sort(localTags, new CompareByTime());
		
		return makeIndependent(localTags, endInstantOfLastTasks);
	}
	
	List<StartTag> makeIndependent(List<? extends StartTag> tags, Instant endInstantOfLastTasks)
	{
		List<StartTag> result = new LinkedList<StartTag>();
		
		Map<String, StartTag> lastStartTagForUser = new LinkedHashMap<String, StartTag>();

		for (StartTag tag: tags)
		{
			StartTag lastStartTag = lastStartTagForUser.get(tag.getWho());
			
			if (null != lastStartTag)
			{
				StartTag enrichedLastStartTag = new StartTag(lastStartTag.getWho(), lastStartTag.getWhen(), lastStartTag.getWhat(), tag.getWhen());
				
				result.add(enrichedLastStartTag);
			}

			lastStartTagForUser.put(tag.getWho(), tag);
		}
		
		for (StartTag tag: lastStartTagForUser.values())
		{
			result.add(new StartTag(tag.getWho(), tag.getWhen(), tag.getWhat(), endInstantOfLastTasks));
		}

		return result;
	}
}
