package bacond.timeslice.web.gwt.client.server;

import static bacond.timeslice.web.gwt.client.jsonutil.JsonTypingHelper.doubleMemberOrThrow;
import static bacond.timeslice.web.gwt.client.jsonutil.JsonTypingHelper.stringMemberOrThrow;
import bacond.timeslice.web.gwt.client.beans.TaskTotal;
import bacond.timeslice.web.gwt.client.util.ITransform;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class TaskTotalFromJson implements ITransform<JSONValue, TaskTotal>
{
	public TaskTotal apply(JSONValue value)
	{
		JSONObject object = value.isObject();

		if (null == object)
		{
			throw new RuntimeException("JSON value for TaskTotal was not an object as expected.");
		}

		String what = stringMemberOrThrow(object, "what");
		Double durationms = doubleMemberOrThrow(object, "durationms");
		String who = stringMemberOrThrow(object, "who");

		return new TaskTotal(who, durationms, what);
	}
}
