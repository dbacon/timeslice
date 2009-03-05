package bacond.timeslicer.web.gwt.client.server;

import java.util.ArrayList;
import java.util.List;

import org.restlet.gwt.Callback;
import org.restlet.gwt.Client;
import org.restlet.gwt.data.MediaType;
import org.restlet.gwt.data.Method;
import org.restlet.gwt.data.Preference;
import org.restlet.gwt.data.Protocol;
import org.restlet.gwt.data.Reference;
import org.restlet.gwt.data.Request;
import org.restlet.gwt.data.Response;
import org.restlet.gwt.resource.JsonRepresentation;
import org.restlet.gwt.resource.StringRepresentation;

import bacond.timeslicer.web.gwt.client.beans.StartTag;
import bacond.timeslicer.web.gwt.client.entry.AsyncResult;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class ItemJsonSvc
{
	String baseSvcUri = "http://localhost:8082";
	
	public String getBaseSvcUri()
	{
		return baseSvcUri;
	}

	public void setBaseSvcUri(String baseSvcUri)
	{
		this.baseSvcUri = baseSvcUri;
	}

	private Request createJsonWsRequest(Method method, String uri)
	{
		Request req = new Request(method, new Reference(uri));
		req.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
		return req;
	}

	/**
	 * TODO: implement and use JSON repr POST instead of custom text format.
	 * 
	 * @param key
	 * @param taskDescription
	 * @param ender
	 */
	public void beginAddItem(String instantString, String taskDescription, final IRequestEnder<Void> ender)
	{
		StringRepresentation entity = new StringRepresentation(
				/*"key=" + key + "\n" + "project=" + project + "\n" + */"what=" + taskDescription);
		entity.setMediaType(MediaType.TEXT_PLAIN);
		
		Client client = new Client(Protocol.HTTP);
		client.post(new Reference(baseSvcUri + "/items"), entity, new Callback()
		{
			public void onEvent(Request request, Response response)
			{
				if (response.getStatus().isSuccess())
				{
					ender.end(AsyncResult.returnedVoid(response.getStatus()));
				}
				else
				{
					ender.end(AsyncResult.<Void>threw(response.getStatus(), new RuntimeException("HTTP code " + response.getStatus().getCode())));
				}
			}
		});
	}
	
	/**
	 * TODO: export only the JSON conversion bits and factor the rest to a base class.
	 */
	public void beginRefreshItems(int maxSize, final IRequestEnder<List<StartTag>> ender)
	{
		int max = maxSize;
		SortDir sortDir = SortDir.desc;
		
		new Client(Protocol.HTTP).handle(createJsonWsRequest(Method.GET, baseSvcUri + "/items?sortdir=" + sortDir.name() + "&max=" + max + ""), new Callback()
		{
			public void onEvent(Request request, Response response)
			{
				try
				{
					if (response.getStatus().isSuccess())
					{
						JsonRepresentation jsonEntity = response.getEntityAsJson();
						JSONValue jsonValue = jsonEntity.getValue();
						JSONArray jsonArray = jsonValue.isArray();
						
						List<StartTag> result = new ArrayList<StartTag>();
						if (null != jsonArray)
						{
							for (int i = 0; i < jsonArray.size(); ++i)
							{
								JSONValue value = jsonArray.get(i);
								JSONObject object = value.isObject();
								
								if (null != object && object.containsKey("what") && object.containsKey("when"))
								{
									JSONString whatString = object.get("what").isString();
									JSONString whenString = object.get("when").isString();
									Double durationMs = null;
									String until = null;
									
									if (null != object && object.containsKey("until"))
									{
										JSONValue untilValue = object.get("until");
										
										JSONString untilString = untilValue.isString();
										if (null != untilString)
										{
											until = untilString.stringValue();
										}
									}
									
									if (null != object && object.containsKey("durationms"))
									{
										JSONValue durationValue = object.get("durationms");
										
										JSONNumber jsonNumber = durationValue.isNumber();
										if (null != jsonNumber)
										{
											durationMs = jsonNumber.doubleValue();
										}
									}
									
									if (null != whenString && null != whatString)
									{
										result.add(new StartTag(whenString.stringValue(), until, durationMs, whatString.stringValue()));
									}
									else
									{
										throw new RuntimeException("Attribute 'when' or 'what' (or both) was not a JSONString value.");
									}
								}
								else
								{
									throw new RuntimeException("Member of JSONArray was not an object with both 'when' and 'what' set.");
								}
							}

							ender.end(AsyncResult.returned(response.getStatus(), result));
						}
						else
						{
							throw new RuntimeException("Representation was not a JSONArray");
						}
					}
					else
					{
						throw new RuntimeException("HTTP response was not a success status");
					}
				}
				catch (Exception e)
				{
					ender.end(AsyncResult.<List<StartTag>>threw(response.getStatus(), e));
				}
			}
		});
	}
}
