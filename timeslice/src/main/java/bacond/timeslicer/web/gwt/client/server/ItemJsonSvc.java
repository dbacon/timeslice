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

import bacond.timeslicer.web.gwt.client.beans.Item;
import bacond.timeslicer.web.gwt.client.entry.AsyncResult;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class ItemJsonSvc
{
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
	 * @param project
	 * @param ender
	 */
	public void beginAddItem(String key, String project, final IRequestEnder<Void> ender)
	{
		StringRepresentation entity = new StringRepresentation("key=" + key + ";project=" + project);
		entity.setMediaType(MediaType.TEXT_PLAIN);
		
		Client client = new Client(Protocol.HTTP);
		client.post(new Reference("http://localhost:8082/items"), entity, new Callback()
		{
			public void onEvent(Request request, Response response)
			{
				if (response.getStatus().isSuccess())
				{
					ender.end(AsyncResult.returnedVoid(response.getStatus()));
				}
				else
				{
					ender.end(AsyncResult.<Void>threw(response.getStatus(), new RuntimeException("")));
				}
			}
		});
	}

	/**
	 * TODO: export only the JSON conversion bits and factor the rest to a base class.
	 */
	public void beginRefreshItems(final IRequestEnder<List<Item>> ender)
	{
		new Client(Protocol.HTTP).handle(createJsonWsRequest(Method.GET, "http://localhost:8082/items"), new Callback()
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
						
						List<Item> result = new ArrayList<Item>();
						if (null != jsonArray)
						{
							
							for (int i = 0; i < jsonArray.size(); ++i)
							{
								JSONValue value = jsonArray.get(i);
								JSONObject object = value.isObject();
								
								if (null != object && object.containsKey("key") && object.containsKey("project"))
								{
									JSONString keyString = object.get("key").isString();
									JSONString projectString = object.get("project").isString();
									
									if (null != keyString && null != projectString)
									{
										result.add(new Item(keyString.stringValue(), projectString.stringValue()));
									}
									else
									{
										throw new RuntimeException("Attribute 'key' or 'project' (or both) was not a JSONString value.");
									}
								}
								else
								{
									throw new RuntimeException("Member of JSONArray was not an object with both 'key' and 'project' set.");
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
					ender.end(AsyncResult.<List<Item>>threw(response.getStatus(), e));
				}
			}
		});
	}
}
