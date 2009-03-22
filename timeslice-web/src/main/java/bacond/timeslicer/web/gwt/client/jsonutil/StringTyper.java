package bacond.timeslicer.web.gwt.client.jsonutil;

import bacond.timeslicer.web.gwt.client.util.ITransform;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class StringTyper extends BaseTyper<JSONString> implements ITransform<JSONValue, String>
{
	public static final StringTyper Instance = new StringTyper();
	
	public String apply(JSONValue jsonValue)
	{
		return throwIfNull(jsonValue.isString()).stringValue();
	}
}