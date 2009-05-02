package bacond.timeslicer.svc.tasktotal;

import java.util.Collection;

import bacond.lib.util.ITransform;
import bacond.timeslicer.app.tasktotal.TaskTotal;

public class TextPlainTaskTotalsFormatter implements ITransform<Collection<TaskTotal>, String>
{
	@Override
	public String apply(Collection<TaskTotal> r)
	{
		StringBuilder sb = new StringBuilder("[").append("\n");

		for (TaskTotal taskTotal: r)
		{
			sb.append(new TextPlainTaskTotalFormatter().apply(taskTotal)).append("\n");
		}

		return sb.append("]").toString();
	}

}
