package bacond.timeslicer.app.restlet.resource;

import java.util.Comparator;

import bacond.timeslicer.app.dto.StartTag;

public class CompareByTime implements Comparator<StartTag>
{
	public static final CompareByTime Instance = new CompareByTime();
	
	@Override
	public int compare(StartTag o1, StartTag o2)
	{
		return o1.getWhen().compareTo(o2.getWhen());
	}
}
