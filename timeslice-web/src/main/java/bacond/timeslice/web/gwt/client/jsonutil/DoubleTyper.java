package bacond.timeslice.web.gwt.client.jsonutil;

import bacond.timeslice.web.gwt.client.util.ITransform;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONValue;

public class DoubleTyper extends BaseTyper<JSONNumber> implements ITransform<JSONValue, Double>
{
	public static final DoubleTyper Instance = new DoubleTyper();
	
	public Double apply(JSONValue jsonValue)
	{
		return throwIfNull(jsonValue.isNumber()).doubleValue();
	}
}