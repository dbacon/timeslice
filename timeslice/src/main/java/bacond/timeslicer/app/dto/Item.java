package bacond.timeslicer.app.dto;

import org.json.JSONString;
import org.json.JSONStringer;

public class Item implements JSONString
{
	private String key;
	private String project;
	
	public Item(String key, String project)
	{
		this.key = key;
		this.project = project;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getProject()
	{
		return project;
	}

	public void setProject(String project)
	{
		this.project = project;
	}

	@Override
	public String toJSONString()
	{
		try
		{
			return new JSONStringer()
				.object()
					.key("key")
					.value(key)
					.key("project")
					.value(project)
				.endObject()
				.toString();

		}
		catch (Exception e)
		{
			throw new RuntimeException("JSON representation failed: " + e.getMessage(), e);
		}
	}
	
}
