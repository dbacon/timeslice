package bacond.timeslice.web.gwt.client.jsonutil;

import bacond.timeslice.web.gwt.client.util.ITransform;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class JsonTypingHelper
{
	public static String stringMemberOrThrow(JSONObject jsonObj, String memberName)
	{
		return typedMemberOrThrow(jsonObj, memberName, StringTyper.Instance);
	}

	public static Double doubleMemberOrThrow(JSONObject jsonObj, String memberName)
	{
		return typedMemberOrThrow(jsonObj, memberName, DoubleTyper.Instance);
	}

	public static <T> T typedMemberOrThrow(JSONObject jsonObj, String memberName, ITransform<JSONValue, T> typer)
	{
		JSONValue jsonValue = jsonObj.get(memberName);

		if (null != jsonValue)
		{
			return typer.apply(jsonValue);
		}
		else
		{
			throw new RuntimeException("JSON object did not have expected member '" + memberName + "'");
		}
	}
}
