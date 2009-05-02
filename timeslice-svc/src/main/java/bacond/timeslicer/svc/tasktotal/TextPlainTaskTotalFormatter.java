package bacond.timeslicer.svc.tasktotal;

import bacond.lib.util.ITransform;
import bacond.timeslicer.app.tasktotal.TaskTotal;

public class TextPlainTaskTotalFormatter implements ITransform<TaskTotal, String>
{
	@Override
	public String apply(TaskTotal r)
	{
		return String.format("%s|%s|%d",
				r.getWho(),
				r.getWhat(),
				r.getMillis());
	}
}
