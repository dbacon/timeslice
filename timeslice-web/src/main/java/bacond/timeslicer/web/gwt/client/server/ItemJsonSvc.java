package bacond.timeslicer.web.gwt.client.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.restlet.gwt.Callback;
import org.restlet.gwt.Client;
import org.restlet.gwt.data.ChallengeResponse;
import org.restlet.gwt.data.ChallengeScheme;
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
import bacond.timeslicer.web.gwt.client.beans.TaskTotal;
import bacond.timeslicer.web.gwt.client.entry.AsyncResult;
import bacond.timeslicer.web.gwt.client.util.ITransform;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;

public class ItemJsonSvc
{
	private String baseSvcUri = "http://localhost:8082";
	private String username = "bacond";
	private String password = "123";
	
	public String getBaseSvcUri()
	{
		return baseSvcUri;
	}

	public void setBaseSvcUri(String baseSvcUri)
	{
		this.baseSvcUri = baseSvcUri;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	private Request createJsonWsRequest(Method method, String uri, Map<String, String> queryParams)
	{
		Reference resourceRef = new Reference(uri);

		for (Entry<String, String> pair: queryParams.entrySet())
		{
			resourceRef.addQueryParameter(pair.getKey(), pair.getValue());
		}
		
		Request req = new Request(method, resourceRef);
		
		req.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));

		req.setChallengeResponse(new ChallengeResponse(ChallengeScheme.HTTP_BASIC, getUsername(), getPassword()));
		
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
				"what=" + taskDescription + "\n" +
				"when=" + instantString + "\n");
//				"key=" + key + "\n" +
//				"project=" + project + "\n"
				
		entity.setMediaType(MediaType.TEXT_PLAIN);
		
		Client client = new Client(Protocol.HTTP);
		client.post(new Reference(getBaseSvcUri() + "/items"), entity, new Callback()
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
	 * Kept for compatibility -- see called method.
	 * 
	 * @param maxSize
	 * @param ender
	 */
	public void beginRefreshItems(int maxSize, final IRequestEnder<List<StartTag>> ender)
	{
		beginRefreshItems(maxSize, SortDir.desc, null, null, null, new StartTagFromJson(), ender);
	}
	
	private static void installIfNotNull(Map<String, String> map, String key, String value)
	{
		if (null != value)
		{
			map.put(key, value);
		}
	}
	
	public void beginRefreshSummed(int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, final IRequestEnder<List<TaskTotal>> ender)
	{
	}
	
	/**
	 * TODO: export only the JSON conversion bits and factor the rest to a base class.
	 */
	public <T> void beginRefreshItems(int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, final ITransform<JSONValue, T> itemFromJson, final IRequestEnder<List<T>> ender)
	{
		String procTypeString = null;
		if (null != procType)
		{
			procTypeString = procType.name();
		}
		
		Map<String, String> params = new LinkedHashMap<String, String>();
		installIfNotNull(params, "sortdir", sortDir.name());
		installIfNotNull(params, "pagesize", "" + maxSize);
		installIfNotNull(params, "proctype", procTypeString);
		installIfNotNull(params, "mintime", startingInstant);
		installIfNotNull(params, "maxtime", endingInstant);
		
//		final StartTagFromJson startTagFromJson = new StartTagFromJson();
		
		new Client(Protocol.HTTP).handle(createJsonWsRequest(Method.GET, getBaseSvcUri() + "/items", params), new Callback()
		{
			public void onEvent(Request request, Response response)
			{
				try
				{
					if (!response.getStatus().isSuccess())
					{
						throw new RuntimeException("HTTP response was not a success status");
					}

					JsonRepresentation jsonEntity = response.getEntityAsJson();
					
					if (null == jsonEntity)
					{
						throw new RuntimeException("No representation was available.");
					}

					JSONArray jsonArray = jsonEntity.getValue().isArray();

					if (null == jsonArray)
					{
						throw new RuntimeException("Representation was not a JSONArray");
					}
					
					List<T> result = new ArrayList<T>();

					for (int i = 0; i < jsonArray.size(); ++i)
					{
						T item = itemFromJson.apply(jsonArray.get(i));

						if (null != item)
						{
							result.add(item);
						}
					}

					ender.end(AsyncResult.returned(response.getStatus(), result));
				}
				catch (Exception e)
				{
					ender.end(AsyncResult.<List<T>>threw(response.getStatus(), e));
				}
			}
		});
	}

	public void beginUpdate(StartTag editedStartTag, final IRequestEnder<Void> ender)
	{
		StringRepresentation entity = new StringRepresentation(
				"key=" + editedStartTag.getInstantString() + "\n" +
				"what=" + editedStartTag.getDescription() + "\n" +
				"");
		entity.setMediaType(MediaType.TEXT_PLAIN);
		
		Client client = new Client(Protocol.HTTP);
		client.put(new Reference(getBaseSvcUri() + "/items/" + editedStartTag.getInstantString()), entity, new Callback()
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

}
