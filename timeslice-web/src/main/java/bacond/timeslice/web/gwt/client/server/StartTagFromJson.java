package bacond.timeslice.web.gwt.client.server;

import static bacond.timeslice.web.gwt.client.jsonutil.JsonTypingHelper.doubleMemberOrThrow;
import static bacond.timeslice.web.gwt.client.jsonutil.JsonTypingHelper.stringMemberOrThrow;
import bacond.timeslice.web.gwt.client.beans.StartTag;
import bacond.timeslice.web.gwt.client.util.ITransform;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class StartTagFromJson implements ITransform<JSONValue, StartTag>
{
	public StartTag apply(JSONValue value)
	{
		JSONObject object = value.isObject();

		if (null == object)
		{
			throw new RuntimeException("JSON value for StartTag was not an object as expected.");
		}
		
		String when = stringMemberOrThrow(object, "when");
		String until = stringMemberOrThrow(object, "until");
		Double duration = doubleMemberOrThrow(object, "durationms"); // used to be optional -- needs support in typing utils.
		String what = stringMemberOrThrow(object, "what"); // used to be optional -- needs support in typing utils.
		
		// validation -- check required fields.
		if (null != when && null != what)
		{
		}
		else
		{
			throw new RuntimeException("Attribute 'when' or 'what' (or both) was not a JSONString value.");
		}
		
		return new StartTag(when, until, duration, what);
	}
}